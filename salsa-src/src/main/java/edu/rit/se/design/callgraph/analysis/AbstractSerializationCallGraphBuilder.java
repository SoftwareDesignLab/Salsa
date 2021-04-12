/*
 * Copyright (c) 2020 - Present. Rochester Institute of Technology
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.rit.se.design.callgraph.analysis;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.IAnalysisCacheView;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerKeyFactory;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.callgraph.propagation.cfa.CallerSiteContext;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.types.Selector;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.CancelException;
import edu.rit.se.design.callgraph.model.AbstractClassModel;
import edu.rit.se.design.callgraph.model.ObjectInputStreamModel;
import edu.rit.se.design.callgraph.model.ObjectOutputStreamModel;
import edu.rit.se.design.dodo.utils.wala.WalaUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashSet;
import java.util.Set;

import static edu.rit.se.design.callgraph.util.NameUtils.*;

public class AbstractSerializationCallGraphBuilder extends SSAPropagationCallGraphBuilder {

    /**
     * constant for using when getting the "this" pointer in an instruction
     */
    protected static final int THIS_POINTER = 1;
    /**
     * enable for printing statements during call graph construction and visitor
     */
    protected static boolean DEBUG_SALSA_CG = false;
    protected static boolean DEBUG_SALSA_VISITOR = DEBUG_SALSA_CG | false;
    /**
     * models for the ObjectInputStream and ObjectOutputStream classes
     */
    protected final ObjectInputStreamModel objectInputStreamModel;
    protected final ObjectOutputStreamModel objectOutputStreamModel;

    /**
     * Set of model nodes to check against
     */
    protected final Set<CGNode> models;


    /**
     * Caching results
     */
    protected final IAnalysisCacheView cache;


    /**
     * worklist for serialization and deserialization: (caller, invoke instruction, target)
     */
    protected final Set<Triple<CGNode, SSAAbstractInvokeInstruction, CGNode>> serializationWorkList;
    protected final Set<Triple<CGNode, SSAAbstractInvokeInstruction, CGNode>> deserializationWorkList;

    /**
     * The types of the primary(untainted) propagation graph and secondary(tainted) propagation type
     */
    protected final PointerAnalysisPolicy defaultPaPolicy;
    protected final PointerAnalysisPolicy taintedPaPolicy;

    protected AbstractSerializationCallGraphBuilder(IMethod abstractRootMethod,
                                                    AnalysisOptions options,
                                                    IAnalysisCacheView cache,
                                                    PointerKeyFactory pointerKeyFactory,
                                                    PointerAnalysisPolicy defaultPaPolicy,
                                                    PointerAnalysisPolicy taintedPaPolicy) {
        super(abstractRootMethod, options, cache, pointerKeyFactory);
        this.models = new HashSet<>();
        this.cache = cache;
        this.defaultPaPolicy = defaultPaPolicy;
        this.taintedPaPolicy = taintedPaPolicy;
        this.deserializationWorkList = new HashSet<>();
        this.serializationWorkList = new HashSet<>();
        this.objectInputStreamModel = new ObjectInputStreamModel(cha, options, cache);
        this.objectOutputStreamModel = new ObjectOutputStreamModel(cha, options, cache);
    }


    public Set<Triple<CGNode, SSAAbstractInvokeInstruction, CGNode>> getSerializationWorkList() {
        return serializationWorkList;
    }

    public Set<Triple<CGNode, SSAAbstractInvokeInstruction, CGNode>> getDeserializationWorkList() {
        return deserializationWorkList;
    }

    /**
     * @param caller the caller node
     * @param site
     * @param recv
     * @param iKey   an abstraction of the receiver of the call (or null if not applicable)
     * @return the CGNode to which this particular call should dispatch.
     */
    @Override
    protected CGNode getTargetForCall(CGNode caller, CallSiteReference site, IClass recv, InstanceKey[] iKey) {
        CGNode target = super.getTargetForCall(caller, site, recv, iKey);
        try {
            // verifies whether the resolved target should be replaced by our model
            target = replaceIfNeeded(caller, site, target);
        } catch (CancelException e) {
            target = null;
            e.printStackTrace();
        }
        return target;
    }


    /**
     * Verifies whether the call target should be replaced by our models for {@link java.io.ObjectInputStream} and {@link java.io.ObjectOutputStream} classes.
     * Only replaces for call sites at the application scope.
     *
     * @param caller the caller of the invocation
     * @param target the cg node target of an invocation
     * @return returns the param target as is or a synthetic node to our our models for {@link java.io.ObjectInputStream} and {@link java.io.ObjectOutputStream} classes in case the target has to be replaced.
     */
    private CGNode replaceIfNeeded(CGNode caller, CallSiteReference site, CGNode target) throws CancelException {
        if (caller == null || target == null) return target;

        IMethod targetMethod = target.getMethod();
        TypeReference typeRef = targetMethod.getDeclaringClass().getReference();
        Selector targetSelector = targetMethod.getSelector();
        if (WalaUtils.isApplicationScope(caller)) {
            if ((typeRef.equals(JavaIoObjectInputStream) && targetSelector.equals(readObjectSelector)) ||
                    (typeRef.equals(JavaIoObjectOutputStream) && targetSelector.equals(writeObjectSelector))) {
                AbstractClassModel model = typeRef.equals(JavaIoObjectInputStream) ? objectInputStreamModel : objectOutputStreamModel;
                Set<Triple<CGNode, SSAAbstractInvokeInstruction, CGNode>> worklist = typeRef.equals(JavaIoObjectInputStream) ? deserializationWorkList : serializationWorkList;
                CGNode newTarget = callGraph.findOrCreateNode(model.getMethod(targetSelector), new CallerSiteContext(caller, site));
                for (SSAAbstractInvokeInstruction call : caller.getIR().getCalls(site)) {
                    worklist.add(new ImmutableTriple<>(caller, call, newTarget));
                }
                if (DEBUG_SALSA_CG) System.out.println("Replacing " + target + " to " + newTarget);

                // adds to the set of model methods
                models.add(newTarget);

                // invalidate previous cache
                callGraph.getAnalysisCache().invalidate(targetMethod, target.getContext());
                return newTarget;
            }
        }
        return target;
    }
}
