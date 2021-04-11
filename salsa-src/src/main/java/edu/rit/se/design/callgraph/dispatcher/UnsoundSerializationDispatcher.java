package edu.rit.se.design.callgraph.dispatcher;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;

import java.util.Set;

public class UnsoundSerializationDispatcher implements IDispatcher {
    /**
     * A method that  computes the set of possible instantiations based on the current method invocation.
     * Client analyses provide a concrete implementation for this method in order to help with their precision (slightly) instead of using solely CHA.
     *
     * @param node
     * @param invokeInstruction
     * @return
     */
    @Override
    public Set<IClass> computePossibleTypesForCall(CGNode node, SSAAbstractInvokeInstruction invokeInstruction) {
        return null;
    }
}
