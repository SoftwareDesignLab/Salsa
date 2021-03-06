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

package edu.rit.se.design.callgraph.model;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.IAnalysisCacheView;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.collections.HashSetFactory;

import static edu.rit.se.design.callgraph.util.NameUtils.readObjectSelector;

public class ObjectInputStreamModel extends AbstractClassModel {


    public ObjectInputStreamModel(IClassHierarchy cha, AnalysisOptions options, IAnalysisCacheView cache) {
        super(cha, options, cache, "ObjectInputStream");
    }

    @Override
    protected void initSyntheticMethods() {
        try {
            IMethod actualReadObjectMethod = this.originalClass.getMethod(readObjectSelector);
            this.methods = HashSetFactory.make();
            this.methods.add(
                    new MethodModel(
                            readObjectSelector,
                            actualReadObjectMethod.getDeclaredExceptions(),
                            this,
                            this.originalClass.getClassHierarchy(),
                            options,
                            cache)
            );
        } catch (InvalidClassFileException e) {
            throw new IllegalStateException(e);
        }
    }
}