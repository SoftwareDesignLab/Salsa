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

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.debug.Assertions;

import java.util.Iterator;
import java.util.Objects;

/**
 * To represent a "fake" instance key (synthetic).
 * This is used to instantiate tainted pointers for creating suitable call graphs for serialization/deserialization analysis.
 * Joanna C. S. Santos (jds5109@rit.edu)
 */
public class FakeInstanceKey implements InstanceKey {

    private IClass serializableType;

    public FakeInstanceKey(IClass serializableType) {
        this.serializableType = serializableType;
    }

    @Override
    public IClass getConcreteType() {
        return this.serializableType;
    }

    @Override
    public String toString() {
        return "FakeInstanceKey{" + serializableType + "}";
    }

    @Override
    public Iterator<Pair<CGNode, NewSiteReference>> getCreationSites(CallGraph CG) {
        Assertions.UNREACHABLE("You shouldn't be calling this because this variable had no previous allocation (synthetic)");
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FakeInstanceKey that = (FakeInstanceKey) o;
        return Objects.equals(serializableType, that.serializableType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serializableType);
    }
}