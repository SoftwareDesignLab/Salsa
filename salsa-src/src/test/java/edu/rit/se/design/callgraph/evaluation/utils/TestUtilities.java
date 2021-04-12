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

package edu.rit.se.design.callgraph;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.types.*;
import com.ibm.wala.util.graph.traverse.DFSAllPathsFinder;
import com.ibm.wala.util.strings.Atom;
import edu.rit.se.design.callgraph.analysis.PointerAnalysisPolicy;
import edu.rit.se.design.callgraph.serializer.ICallGraphSerializer;
import edu.rit.se.design.callgraph.serializer.JDynCallGraphSerializer;
import edu.rit.se.design.dodo.utils.viz.GraphVisualizer;
import org.junit.Assert;

import java.io.File;
import java.util.List;
import java.util.Set;



public class TestUtilities {

    public static final String TC_ROOT_FOLDER = System.getProperty("user.home") + "/Documents/Portfolio/GitHub/fse-2021-serialization/dataset/build/";
    public static final String EXCLUSIONS_FILE = "exclusions.txt";
    public static final String SOAP_2021_STATIC_CGS_FOLDER = System.getProperty("user.home") + "/Documents/Portfolio/GitHub/soap-2021-paper/static-cgs";


    public static int findAllPath(CallGraph cg, MethodReference from, MethodReference to) {
        CGNode fromNode = cg.getNodes(from).iterator().next();
        DFSAllPathsFinder<CGNode> finder = new DFSAllPathsFinder<>(cg, fromNode, node -> node.getMethod().getReference().equals(to));
        int count = 0;
        List<CGNode> path;
        while ((path = finder.find()) != null) {
            System.err.println(path);
            count++;
        }
        return count;
    }

    /**
     * Creates a method reference object needed for performing testing if a given method is in the call graph.
     *
     * @param cl         class loader (application, extension, primordial)
     * @param className  class name in bytecode format (i.e., L/java/lang/String)
     * @param methodName method name
     * @param descriptor descriptor in bytecode format (e.g. (Ljava/lang/String)V)
     * @return a {@link MethodReference}
     */
    public static MethodReference createMethodRef(ClassLoaderReference cl, String className, String methodName, String descriptor) {
        TypeName typeName = TypeName.string2TypeName(className);
        TypeReference tref = TypeReference.findOrCreate(cl, typeName);
        Atom mn = Atom.findOrCreateUnicodeAtom(methodName);
        Descriptor md = Descriptor.findOrCreateUTF8(descriptor);
        return MethodReference.findOrCreate(tref, mn, md);
    }

    /**
     * Asserts that there is a direct call from node n to a node x.
     *
     * @param cg
     * @param from
     * @param to
     */
    public static void checkDirectCall(CallGraph cg, MethodReference from, MethodReference to) {
        Set<CGNode> fromNodes = cg.getNodes(from);
        Set<CGNode> toNodes = cg.getNodes(to);
        Assert.assertTrue("Missing " + from.getSignature() + " from call graph", fromNodes.size() == 1);
        Assert.assertTrue("Missing " + to.getSignature() + " from call graph", toNodes.size() == 1);
        Assert.assertTrue(cg.hasEdge(fromNodes.iterator().next(), toNodes.iterator().next()));
    }


    private static IMethod getSingleEntrypointMethod(CallGraph cg){
        return cg.getEntrypointNodes().iterator().next().getMethod();
    }

    public static void saveCallGraphs(String outputFolder, CallGraph computedCg, PointerAnalysisPolicy policy, String sampleName, String approachName) {
        String filepath = String.format("./target/%s_%s.dot", sampleName, policy);
        File outputDotFile = new File(filepath);
        new GraphVisualizer<CGNode>("Call graph view for " + sampleName,
                GraphVisualizer.getDefaultCgNodeLabeller(),
                GraphVisualizer.getDefaultCgNodeHighlighter(),
                null,
                GraphVisualizer.getDefaultCgNodeRemover(computedCg))
                .generateVisualGraph(computedCg, outputDotFile);

        String mainClass = getSingleEntrypointMethod(computedCg)
                .getDeclaringClass()
                .getName()
                .toString()
                .substring(11);
        String projectName = sampleName.split("-")[0];
        String filename = String.format("%s-%s-%s.txt", approachName, policy.toString(), mainClass);
        File txtFile = new File(String.format("%s/%s/%s", outputFolder, projectName, filename));

        ICallGraphSerializer cgSerializer = new JDynCallGraphSerializer(); //new JavaCallGraphSerializer();
        cgSerializer.save(computedCg, txtFile);
    }

}
