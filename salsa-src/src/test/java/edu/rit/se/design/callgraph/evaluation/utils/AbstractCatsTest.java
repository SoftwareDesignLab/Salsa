package edu.rit.se.design.callgraph.evaluation.utils;

import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.callgraph.CallGraphStats;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.types.MethodReference;
import edu.rit.se.design.callgraph.analysis.PointerAnalysisPolicy;
import edu.rit.se.design.callgraph.serializer.JavaCallGraphSerializer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.ibm.wala.types.ClassLoaderReference.Application;
import static com.ibm.wala.types.ClassLoaderReference.Primordial;
import static edu.rit.se.design.callgraph.TestUtilities.checkDirectCall;
import static edu.rit.se.design.callgraph.TestUtilities.createMethodRef;
import static edu.rit.se.design.callgraph.analysis.PointerAnalysisPolicy.PolicyType.ZeroXCFA;
import static edu.rit.se.design.callgraph.analysis.PointerAnalysisPolicy.PolicyType.nCFA;
import static edu.rit.se.design.callgraph.evaluation.utils.CATSTestCases.*;
import static java.lang.String.format;
import static java.lang.String.join;

/**
 * Evaluates a call graph construction algorithm using the Call Graph & Assessement Suite (CATS).
 *
 * @author Joanna C. S. Santos (jds5109@rit.edu)
 */
public abstract class AbstractCatsTest {
    private final String outputFolder;
    private final Map<String, Pair<MethodReference, MethodReference>> expectedResults;
    private final String approachName;
    private final PointerAnalysisPolicy[] taintedPolicies = new PointerAnalysisPolicy[]{
            new PointerAnalysisPolicy(ZeroXCFA, 1),
            new PointerAnalysisPolicy(nCFA, 1),
            new PointerAnalysisPolicy(nCFA, 2)
    };

    protected AbstractCatsTest(String outputFolder, String approachName) {
        this.outputFolder = outputFolder;
        this.approachName = approachName;
        this.expectedResults = new HashMap<>();
        expectedResults.put(CASE_STUDY_SER1,
                new ImmutablePair<>(
                        createMethodRef(Application, "Lser/Demo", "writeObject", "(Ljava/io/ObjectOutputStream;)V"),
                        createMethodRef(Primordial, "Ljava/io/ObjectOutputStream", "defaultWriteObject", "()V")
                )
        );
        expectedResults.put(CASE_STUDY_SER2,
                new ImmutablePair<>(
                        createMethodRef(Application, "Lser/Demo", "writeObject", "(Ljava/io/ObjectOutputStream;)V"),
                        createMethodRef(Primordial, "Ljava/io/ObjectOutputStream", "defaultWriteObject", "()V")
                )
        );
        expectedResults.put(CASE_STUDY_SER3,
                new ImmutablePair<>(
                        createMethodRef(Application, "Lser/Demo", "writeObject", "(Ljava/io/ObjectOutputStream;)V"),
                        createMethodRef(Primordial, "Ljava/io/ObjectOutputStream", "defaultWriteObject", "()V")
                )
        );
        expectedResults.put(CASE_STUDY_SER4,
                new ImmutablePair<>(
                        createMethodRef(Application, "Lser/Demo", "readObject", "(Ljava/io/ObjectInputStream;)V"),
                        createMethodRef(Primordial, "Ljava/io/ObjectInputStream", "defaultReadObject", "()V")
                )
        );
        expectedResults.put(CASE_STUDY_SER5,
                new ImmutablePair<>(
                        createMethodRef(Application, "Lser/Demo", "readObject", "(Ljava/io/ObjectInputStream;)V"),
                        createMethodRef(Primordial, "Ljava/io/ObjectInputStream", "defaultReadObject", "()V")
                )
        );
        expectedResults.put(CASE_STUDY_SER6,
                new ImmutablePair<>(
                        createMethodRef(Application, "Lser/Demo", "writeReplace", "()Ljava/lang/Object;"),
                        createMethodRef(Application, "Lser/Demo", "replace", "()Ljava/lang/Object;")
                )
        );
        expectedResults.put(CASE_STUDY_SER7,
                new ImmutablePair<>(
                        createMethodRef(Application, "Lser/Demo", "readResolve", "()Ljava/lang/Object;"),
                        createMethodRef(Application, "Lser/Demo", "replace", "()Ljava/lang/Object;")
                )
        );
        expectedResults.put(CASE_STUDY_SER8,
                new ImmutablePair<>(
                        createMethodRef(Application, "Lser/Demo", "validateObject", "()V"),
                        createMethodRef(Application, "Lser/Demo", "callback", "()V")
                )
        );
        expectedResults.put(CASE_STUDY_SER9,
                new ImmutablePair<>(
                        createMethodRef(Application, "Lser/Superclass", "<init>", "()V"),
                        createMethodRef(Application, "Lser/Superclass", "callback", "()V")
                )
        );
    }

    @BeforeAll
    public static void printTestHeader() {
        System.out.println("Approach\tTC\tPolicy\t# Nodes\t#Edges");
    }

    @TestFactory
    public Collection<DynamicTest> jcgTests() {
        Collection<DynamicTest> dynamicTests = new ArrayList<>();
        for (String projectPath : expectedResults.keySet()) {
            for (PointerAnalysisPolicy policy : taintedPolicies) {
                String projectName = FilenameUtils.getBaseName(projectPath).split("-")[0];
                String testName = join("_", projectName, approachName, policy.toString());
                DynamicTest dTest = DynamicTest.dynamicTest(testName, () -> {
                    CallGraph cg = computeCallGraph(projectPath, policy);
                    Pair<MethodReference, MethodReference> edge = expectedResults.get(projectPath);
                    checkDirectCall(cg, edge.getLeft(), edge.getRight());
                    saveCallGraph(cg, projectName, policy);
                });
                dynamicTests.add(dTest);
            }

        }
        return dynamicTests;
    }

    private void saveCallGraph(CallGraph computedCg, String sampleName, PointerAnalysisPolicy policy) {
        CallGraphStats.CGStats stats = CallGraphStats.getCGStats(computedCg);
        System.out.println(format("%s\t%s\t%s\t%s\t%s", approachName, sampleName, policy, stats.getNNodes(), stats.getNEdges()));


        String txtFilename;
        try {
            // if the int parsing succeeds, test case represents a test from the JCG suite (CATS)
            int i = Integer.valueOf(sampleName.substring(3, 4));
            txtFilename = format("Ser%d-%s-%s", i, approachName, policy.toString());
        } catch (NumberFormatException ex) {
            // sample is not from JCG test suite
            txtFilename = format("%s-%s-%s", sampleName, approachName, policy.toString());
        }

        File txtFile = new File(format("%s/%s.txt", outputFolder, txtFilename));
        JavaCallGraphSerializer cgSerializer = new JavaCallGraphSerializer();
        cgSerializer.save(computedCg, txtFile);
    }


    protected abstract CallGraph computeCallGraph(String projectPath, PointerAnalysisPolicy policy) throws IOException, ClassHierarchyException, CallGraphBuilderCancelException;


}
