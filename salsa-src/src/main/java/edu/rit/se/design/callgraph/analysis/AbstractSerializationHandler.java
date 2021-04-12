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

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.MonitorUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class for the concrete implementations for how serialization should be handled in terms of points-to analysis.
 *
 * @author Joanna C. S. Santos
 */
public abstract class AbstractSerializationHandler {
    protected final SSAPropagationCallGraphBuilder builder;
    protected final Set<IClass> serializableClasses;

    public AbstractSerializationHandler(SSAPropagationCallGraphBuilder builder) {
        this.builder = builder;
        this.serializableClasses = computeSerializableClasses();
    }

    public abstract void handleSerializationRelatedFeatures(MonitorUtil.IProgressMonitor monitor);

    /**
     * Computes the set of serializable classes.
     */

    private Set<IClass> computeSerializableClasses() {
        IClassHierarchy cha = builder.getClassHierarchy();
        Set<IClass> classes = new HashSet<>();
        IClass serialInterface = cha.lookupClass(TypeReference.JavaIoSerializable);
        // iterates all classes in the classpath to compute what is serializable or not
        for (IClass c : cha) {
            if (cha.implementsInterface(c, serialInterface)) {
                classes.add(c);
            }
        }

        return classes;
    }
}
