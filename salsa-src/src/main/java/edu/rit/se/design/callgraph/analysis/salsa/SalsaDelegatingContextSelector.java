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

package edu.rit.se.design.callgraph.analysis.salsa;


import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Context;
import com.ibm.wala.ipa.callgraph.ContextSelector;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.util.intset.IntSet;


/**
 * It controls the context selection policy via delegation.
 *
 * @author Reese A. Jones (raj8065@rit.edu)
 * @author Joanna C. S. Santos (jds5109@rit.edu)
 * @see ContextSelector
 */
public class SalsaDelegatingContextSelector implements ContextSelector {

    private ContextSelector A;
    private ContextSelector B;
    private SalsaSSAPropagationCallGraphBuilder builder;

    public SalsaDelegatingContextSelector(
            SalsaSSAPropagationCallGraphBuilder builder,
            ContextSelector A,
            ContextSelector B) {

        if (A == null)
            throw new IllegalArgumentException("Untainted ContextInterpreter for ContextInterpreter delegation is null");
        if (B == null)
            throw new IllegalArgumentException("Tainted ContextInterpreter for ContextInterpreter delegation is null");
        if (builder == null)
            throw new IllegalArgumentException("Builder for ContextInterpreter delegation is null");

        this.builder = builder;
        this.A = A;
        this.B = B;
    }

    /**
     * Delegation-based implementation.
     * If the caller is
     *
     * @param caller           the node making the invocation
     * @param site             callsite
     * @param callee           the node corresponding to the invocation target
     * @param actualParameters the passes instance types
     * @return the {@link Context} to be used in this call.
     */
    @Override
    public Context getCalleeTarget(CGNode caller, CallSiteReference site, IMethod callee, InstanceKey[] actualParameters) {
        if (!builder.isSyntheticModel(caller))
            return A.getCalleeTarget(caller, site, callee, actualParameters);

        return B.getCalleeTarget(caller, site, callee, actualParameters);
    }

    @Override
    public IntSet getRelevantParameters(CGNode caller, CallSiteReference site) {
        if (!builder.isSyntheticModel(caller))
            return A.getRelevantParameters(caller, site);

        return B.getRelevantParameters(caller, site);
    }
}
