package edu.rit.se.design.callgraph.dispatcher;


import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.types.TypeReference;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.rit.se.design.callgraph.util.SerializationUtils.isAccessible;
import static edu.rit.se.design.dodo.utils.wala.WalaUtils.isApplicationScope;

/**
 * @author Joanna C. S. Santos
 */
public class SerializationDispatcher implements IDispatcher {

    // parametrization for fine-tuning the performance

    private final IClass serialInterface;
    /**
     * If true, then it filters the sets of possible targets including only classes in the application scope
     * (filtering only if the set of possible targets in an invocation is higher than the pruningThreshold)
     */
    private boolean prune;
    /**
     * Cut-off threshold
     */
    private int pruningThreshold;


    public SerializationDispatcher(IClassHierarchy cha, boolean prune, int pruningThreshold) {
        this.prune = prune;
        this.pruningThreshold = pruningThreshold;
        this.serialInterface = cha.lookupClass(TypeReference.JavaIoSerializable);
    }

    public SerializationDispatcher(IClassHierarchy cha) {
        this(cha, true, 100);
    }


    /**
     * This method computes the set of possible instantiations based on the current method invocation.
     * First it uses CHA to compute the set of possible types, then it keeps only the serializable types that are accessible (following the acessibility rules, private/package/public).
     * That helps with precision (slightly).
     *
     * @param node              call graph node that has the current invocation instruction
     * @param invokeInstruction invoke instruction
     * @return the set of possible types for a given call.
     */
    @Override
    public Set<IClass> computePossibleTypesForCall(CGNode node, SSAAbstractInvokeInstruction invokeInstruction) {
        IClassHierarchy cha = node.getClassHierarchy();
        // the static type of the receiver object
        IClass staticType = cha.lookupClass(invokeInstruction.getDeclaredTarget().getDeclaringClass());
        IClass declaringClass = node.getMethod().getDeclaringClass();

        // uses CHA to compute the possible types
        Set<IMethod> possibleTargets = cha.getPossibleTargets(invokeInstruction.getDeclaredTarget());

        // keeps only the serializable classes and that are accessible
        Set<IClass> concreteTypes = new HashSet<>();
        for (IMethod possibleTarget : possibleTargets) {
            IClass targetIClass = possibleTarget.getDeclaringClass();
            if (cha.implementsInterface(targetIClass, serialInterface)) {
                boolean hasNoImplementation = targetIClass.isInterface() || targetIClass.isAbstract();
                boolean isAccessible = isAccessible(declaringClass, targetIClass);
                if (!hasNoImplementation && isAccessible) {
                    concreteTypes.add(targetIClass);
                }
            }
        }


        // prune away primordial classes if the computed sets are fairly large
        if (prune && concreteTypes.size() > pruningThreshold) {
            concreteTypes = concreteTypes.stream().filter(c -> isApplicationScope(c)).collect(Collectors.toSet());
        }
        return concreteTypes;
    }


}
