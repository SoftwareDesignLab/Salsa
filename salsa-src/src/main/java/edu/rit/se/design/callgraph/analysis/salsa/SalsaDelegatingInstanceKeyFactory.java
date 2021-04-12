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

import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.classLoader.ProgramCounter;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKeyFactory;
import com.ibm.wala.types.TypeReference;


/**
 * @author Reese A. Jones (raj8065@rit.edu)
 * @author Joanna C. S. Santos (jds5109@rit.edu)
 */
public class SalsaDelegatingInstanceKeyFactory implements InstanceKeyFactory {

    private SalsaSSAPropagationCallGraphBuilder builder;
    private InstanceKeyFactory A;
    private InstanceKeyFactory B;

    public SalsaDelegatingInstanceKeyFactory(
            SalsaSSAPropagationCallGraphBuilder builder,
            InstanceKeyFactory A,
            InstanceKeyFactory B) {

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

    @Override
    public InstanceKey getInstanceKeyForAllocation(CGNode node, NewSiteReference allocation) {
        if (!builder.isSyntheticModel(node))
            return A.getInstanceKeyForAllocation(node, allocation);

        return B.getInstanceKeyForAllocation(node, allocation);
    }

    @Override
    public InstanceKey getInstanceKeyForMultiNewArray(CGNode node, NewSiteReference allocation, int dim) {
        if (!builder.isSyntheticModel(node))
            return A.getInstanceKeyForMultiNewArray(node, allocation, dim);

        return B.getInstanceKeyForMultiNewArray(node, allocation, dim);
    }

    @Override
    public <T> InstanceKey getInstanceKeyForConstant(TypeReference type, T S) {
        // Currently the primary InstanceKeyFactory is used,
        // since there is no way to determine which one to use
        return A.getInstanceKeyForConstant(type, S);
    }

    @Override
    public InstanceKey getInstanceKeyForPEI(CGNode node, ProgramCounter instr, TypeReference type) {
        if (!builder.isSyntheticModel(node))
            return A.getInstanceKeyForPEI(node, instr, type);

        return B.getInstanceKeyForPEI(node, instr, type);
    }

    @Override
    public InstanceKey getInstanceKeyForMetadataObject(Object obj, TypeReference objType) {
        // Currently the primary InstanceKeyFactory is used,
        // since there is no way to determine which one to use
        return A.getInstanceKeyForMetadataObject(obj, objType);
    }

    /**
     * This method is needed for making ZeroXContainer ContextSelector. It only accepts ZeroXCFA,
     * so this class cannot be used and one of its InstanceKeyFactorys must directly be used.
     *
     * @return
     */
    public InstanceKeyFactory getPrimaryInstanceKeyFactory() {
        return A;
    }

    /**
     * This method is needed for making ZeroXContainer ContextSelector. It only accepts ZeroXCFA,
     * so this class cannot be used and one of its InstanceKeyFactorys must directly be used.
     *
     * @return
     */
    public InstanceKeyFactory getSecondaryInstanceKeyFactory() {
        return B;
    }
}
