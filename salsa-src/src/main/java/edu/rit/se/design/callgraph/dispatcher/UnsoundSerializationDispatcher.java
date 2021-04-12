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
