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

package edu.rit.se.design.callgraph.evaluation.salsa.rq1.soundness;

import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.MethodReference;
import edu.rit.se.design.callgraph.analysis.PointerAnalysisPolicy;
import edu.rit.se.design.callgraph.analysis.salsa.SalsaZeroXCallGraphBuilder;
import edu.rit.se.design.callgraph.dispatcher.SerializationDispatcher;
import edu.rit.se.design.callgraph.serializer.JavaCallGraphSerializer;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.ibm.wala.types.ClassLoaderReference.Application;
import static com.ibm.wala.types.ClassLoaderReference.Primordial;
import static edu.rit.se.design.callgraph.TestUtilities.*;
import static edu.rit.se.design.callgraph.analysis.PointerAnalysisPolicy.PolicyType.*;
import static edu.rit.se.design.callgraph.evaluation.utils.CATSTestCases.*;
import static edu.rit.se.design.callgraph.util.AnalysisUtils.*;
import static java.lang.String.format;

public class JCGTest {


    private boolean taintBased;
    private CallGraph computedCg;
    private String sampleName;


    private static final PointerAnalysisPolicy policy =
//            new PointerAnalysisPolicy(ZeroXCFA,1);
//            new PointerAnalysisPolicy(nCFA, 1);
            new PointerAnalysisPolicy(nCFA, 2);


    @BeforeClass
    public static void printTestHeader() {
        System.out.println("Approach\tTC\tPolicy\t# Nodes\t#Edges");
    }

    @After
    public void tearDown() throws Exception {
        JavaCallGraphSerializer cgSerializer = new JavaCallGraphSerializer();
        CallGraphStats.CGStats stats = CallGraphStats.getCGStats(computedCg);

        String algorithmName = taintBased ? "Seneca" : "Salsa";
        System.out.println(format("%s\t%s\t%s\t%s\t%s", algorithmName, sampleName, policy, stats.getNNodes(), stats.getNEdges()));


        String approach;
        try {
            // if the int parsing succeeds, test case represents a test from the JCG suite (CATS)
            int i = Integer.valueOf(sampleName.substring(3, 4));
//            approach = format("Ser%d-Seneca-%s-%s", i, algorithmName, policy.toString());
            approach = format("Ser%d-Salsa-%s", i, policy.toString());
        } catch (NumberFormatException ex) {
            // sample is not from JCG test suite
            approach = format("%s-Seneca-%s-%s", algorithmName, sampleName, policy.toString());
        }
        File txtFile = new File(format("%s/%s.txt", SOAP_2021_STATIC_CGS_FOLDER, approach));
        cgSerializer.save(computedCg, txtFile);

    }

    private CallGraph computeCallGraph(boolean taintBased, String sample) throws ClassHierarchyException, IOException, CallGraphBuilderCancelException {

        File exclusions = new File(EXCLUSIONS_FILE);
        // Basic Variables
        AnalysisScope scope = makeAnalysisScope(sample, exclusions);
        IClassHierarchy cha = makeIClassHierarchy(scope);
        AnalysisOptions options = makeAnalysisOptions(scope, cha);
        AnalysisCache cache = makeAnalysisCache();

        CallGraphBuilder builder = SalsaZeroXCallGraphBuilder.make(scope, options, cache, cha, policy);
        CallGraph cg = builder.makeCallGraph(options, /*new CustomMonitor()*/ null);
        this.computedCg = cg;
        this.sampleName = new File(sample).getName();
        this.taintBased = taintBased;
        return cg;
    }


//    @Test
//    public void testFTfJPRunningExample() throws Exception {
//        CallGraph cg = computeCallGraph(false, TestDataSets.RUNNING_EXAMPLE);
//        Assert.assertNotNull(cg);
//        cg = computeCallGraph(true, TestDataSets.RUNNING_EXAMPLE);
//        Assert.assertNotNull(cg);
//    }


//    @Test
//    public void testPLDIRunningExample() throws Exception {
//        CallGraph cg = computeCallGraph(false, TestDataSets.PLDI_RUNNING_EXAMPLE);
//        Assert.assertNotNull(cg);
//        cg = computeCallGraph(true, TestDataSets.PLDI_RUNNING_EXAMPLE);
//        Assert.assertNotNull(cg);
//    }


    //<editor-fold desc="Downcast-based (Salsa)">
    @Test
    public void testSer1DowncastBased() throws Exception {
        CallGraph cg = computeCallGraph(false, CASE_STUDY_SER1);
        MethodReference from = createMethodRef(Application, "Lser/Demo", "writeObject", "(Ljava/io/ObjectOutputStream;)V");
        MethodReference to = createMethodRef(Primordial, "Ljava/io/ObjectOutputStream", "defaultWriteObject", "()V");
        checkDirectCall(cg, from, to);
    }

    @Test
    public void testSer2DowncastBased() throws ClassHierarchyException, CallGraphBuilderCancelException, IOException {
        CallGraph cg = computeCallGraph(false, CASE_STUDY_SER2);
        MethodReference from = createMethodRef(Application, "Lser/Demo", "writeObject", "(Ljava/io/ObjectOutputStream;)V");
        MethodReference to = createMethodRef(Primordial, "Ljava/io/ObjectOutputStream", "defaultWriteObject", "()V");
        checkDirectCall(cg, from, to);
    }

    @Test
    public void testSer3DowncastBased() throws ClassHierarchyException, CallGraphBuilderCancelException, IOException {
        CallGraph cg = computeCallGraph(false, CASE_STUDY_SER3);
        MethodReference from = createMethodRef(Application, "Lser/Demo", "writeObject", "(Ljava/io/ObjectOutputStream;)V");
        MethodReference to = createMethodRef(Primordial, "Ljava/io/ObjectOutputStream", "defaultWriteObject", "()V");
        checkDirectCall(cg, from, to);
    }

    @Test
    public void testSer4DowncastBased() throws ClassHierarchyException, CallGraphBuilderCancelException, IOException {
        CallGraph cg = computeCallGraph(false, CASE_STUDY_SER4);
        MethodReference from = createMethodRef(Application, "Lser/Demo", "readObject", "(Ljava/io/ObjectInputStream;)V");
        MethodReference to = createMethodRef(Primordial, "Ljava/io/ObjectInputStream", "defaultReadObject", "()V");
        checkDirectCall(cg, from, to);
    }

    @Test
    public void testSer5DowncastBased() throws ClassHierarchyException, CallGraphBuilderCancelException, IOException {
        CallGraph cg = computeCallGraph(false, CASE_STUDY_SER5);
        MethodReference from = createMethodRef(Application, "Lser/Demo", "readObject", "(Ljava/io/ObjectInputStream;)V");
        MethodReference to = createMethodRef(Primordial, "Ljava/io/ObjectInputStream", "defaultReadObject", "()V");
        checkDirectCall(cg, from, to);
    }

    @Test
    public void testSer6DowncastBased() throws ClassHierarchyException, CallGraphBuilderCancelException, IOException {
        CallGraph cg = computeCallGraph(false, CASE_STUDY_SER6);
        MethodReference from = createMethodRef(Application, "Lser/Demo", "writeReplace", "()Ljava/lang/Object;");
        MethodReference to = createMethodRef(Application, "Lser/Demo", "replace", "()Ljava/lang/Object;");
        checkDirectCall(cg, from, to);
    }

    @Test
    public void testSer7DowncastBased() throws ClassHierarchyException, CallGraphBuilderCancelException, IOException {
        CallGraph cg = computeCallGraph(false, CASE_STUDY_SER7);
        MethodReference from = createMethodRef(Application, "Lser/Demo", "readResolve", "()Ljava/lang/Object;");
        MethodReference to = createMethodRef(Application, "Lser/Demo", "replace", "()Ljava/lang/Object;");
        checkDirectCall(cg, from, to);
    }

    @Test
    public void testSer8DowncastBased() throws ClassHierarchyException, CallGraphBuilderCancelException, IOException {
        CallGraph cg = computeCallGraph(false, CASE_STUDY_SER8);
        MethodReference from = createMethodRef(Application, "Lser/Demo", "validateObject", "()V");
        MethodReference to = createMethodRef(Application, "Lser/Demo", "callback", "()V");
        checkDirectCall(cg, from, to);
    }

    @Test
    public void testSer9DowncastBased() throws ClassHierarchyException, CallGraphBuilderCancelException, IOException {
        CallGraph cg = computeCallGraph(false, CASE_STUDY_SER9);
        MethodReference from = createMethodRef(Application, "Lser/Superclass", "<init>", "()V");
        MethodReference to = createMethodRef(Application, "Lser/Superclass", "callback", "()V");
        checkDirectCall(cg, from, to);
    }
    //</editor-fold>


    
}