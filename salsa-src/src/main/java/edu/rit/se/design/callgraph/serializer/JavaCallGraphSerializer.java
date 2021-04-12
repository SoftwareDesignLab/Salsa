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

package edu.rit.se.design.callgraph.serializer;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Saves a call graph with the same format as the Java Call Graph tool (https://github.com/gousiosg/java-callgraph).
 *
 * @author Joanna C. S. Santos
 */
public class JavaCallGraphSerializer implements ICallGraphSerializer {
    /**
     * Converts an {@link IMethod} to a string.
     *
     * @param method ther method to be converted
     * @return a string like fully.qualified.class.Name:methodName
     */
    public static String method2String(IMethod method) {
        return String.format("%s:%s",
                method.getDeclaringClass().getName().toString().substring(1).replace("/", "."),
                method.getName().toString());
    }

    /**
     * Saves a file in a format that matches the Java Call Graph tool.
     * (https://github.com/gousiosg/java-callgraph)
     *
     * @param cg         call graph
     * @param outputFile where to save the file
     */
    @Override
    public void save(CallGraph cg, File outputFile) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (CGNode cgNode : cg) {
                IMethod method = cgNode.getMethod();
                cg.getSuccNodes(cgNode).forEachRemaining(target -> {
                    stringBuilder.append(String.format("%s %s\n", method2String(method), method2String(target.getMethod())));
                });
            }

            FileUtils.write(outputFile, stringBuilder.toString(), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
