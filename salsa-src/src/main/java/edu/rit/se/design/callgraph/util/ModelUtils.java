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

package edu.rit.se.design.callgraph.util;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;

import java.util.LinkedList;
import java.util.List;

import static edu.rit.se.design.callgraph.util.NameUtils.readObjectNoDataCallbackSelector;
import static edu.rit.se.design.callgraph.util.NameUtils.validateObjectCallbackSelector;

/**
 * Utilities for abstracting {@link java.io.ObjectInputStream} and {@link java.io.ObjectOutputStream} classes.
 *
 * @author Joanna C. S. Santos (jds5109@rit.edu)
 */
public class ModelUtils {
    public static final boolean DEBUG_SALSA_MODEL_UTILS = true;


    public static List<IMethod> getDeserializationCallbacks(IClass klass) {
        List<IMethod> cbMethods = new LinkedList<>();

        // if class implements the readObject() callback
        IMethod readObjectCallbackMethod = klass.getMethod(NameUtils.readObjectCallbackSelector);
        if (readObjectCallbackMethod != null && readObjectCallbackMethod.getDeclaringClass().equals(klass))
            cbMethods.add(readObjectCallbackMethod);

        // if class implements the readObjectNoData() callback
        IMethod readObjectNoDataCallbackMethod = klass.getMethod(readObjectNoDataCallbackSelector);
        if (readObjectNoDataCallbackMethod != null && readObjectNoDataCallbackMethod.getDeclaringClass().equals(klass))
            cbMethods.add(readObjectNoDataCallbackMethod);

        // if class implements the readResolve() callback
        IMethod readResolveCallbackMethod = klass.getMethod(NameUtils.readResolveCallbackSelector);
        if (readResolveCallbackMethod != null && readResolveCallbackMethod.getDeclaringClass().equals(klass))
            cbMethods.add(readResolveCallbackMethod);

        // if class implements the validateObject() callback
        IMethod validateObjectCallbackMethod = klass.getMethod(validateObjectCallbackSelector);
        if (validateObjectCallbackMethod != null && validateObjectCallbackMethod.getDeclaringClass().equals(klass))
            cbMethods.add(validateObjectCallbackMethod);

        return cbMethods;
    }
}
