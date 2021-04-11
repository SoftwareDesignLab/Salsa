package edu.rit.se.design.callgraph.evaluation.salsa.rq3.performance;

import com.ibm.wala.cast.ir.ssa.AstIRFactory;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.MonitorUtil;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.io.FileUtil;
import edu.rit.se.design.callgraph.analysis.PointerAnalysisPolicy;
import edu.rit.se.design.callgraph.analysis.salsa.SalsaNCFACallGraphBuilder;
import edu.rit.se.design.callgraph.analysis.salsa.SalsaZeroXCallGraphBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.jar.JarFile;

import static edu.rit.se.design.callgraph.analysis.PointerAnalysisPolicy.PolicyType.*;


public class PerformanceTest {
    private static String ROOT = "/Users/joanna/Documents/Portfolio/GitHub/pldi-2021-paper/xcorpus/";

    private static String OSCACHE = ROOT + "oscache-2.4.1/oscache-2.4.1.jar";
    private static String OPENJMS = ROOT + "openjms-0.7.7-beta-1/openjms-0.7.7-beta-1.jar";


    private static String LOG4J = ROOT + "log4j-1.2.16/log4j-1.2.16.jar";
    private static String COMMONSCOLLE = ROOT + "commons-collections-3.2.1/commons-collections-3.2.1.jar";
    private static String HTMLUNIT = ROOT + "htmlunit-2.8/htmlunit-2.8.jar";
    private static String POOKA = ROOT + "pooka-3.0-080505/pooka-3.0-080505.jar";

    private static final String XALAN = ROOT + "xalan-2.7.1/xalan-2.7.1.jar";
    private static final String CASTOR = ROOT + "castor-1.3.1/castor-1.3.1.jar";


    private static final String MEGAMEK = ROOT + "megamek-0.35.18/megamek-0.35.18.jar";


    private static String JFREECHART = ROOT + "jfreechart-1.0.13/jfreechart-1.0.13.jar";


    private static String JMONEY = ROOT + "jmoney-0.4.4/jmoney-0.4.4.jar";
    private static String JGRAPH = ROOT + "jgraphpad-5.10.0.2/jgraphpad-5.10.0.2.jar";
    private static String XERCES = ROOT + "xerces-2.10.0/xerces-2.10.0.jar";

    private static String WEKA = ROOT + "weka-3-7-9/weka-3-7-9.jar";


    private static void computeCallGraph(String sample, CallGraphBuilder builder, AnalysisOptions options) throws CallGraphBuilderCancelException {
        long begin = System.currentTimeMillis();
        CallGraph cg = builder.makeCallGraph(options, new CustomMonitor());
        long end = System.currentTimeMillis();
        System.out.println(builder.getClass().getSimpleName() + "\t" + new File(sample).getName() + "\t" + (end - begin));
    }


    public static void main(String[] args) throws IOException, CallGraphBuilderCancelException, ClassHierarchyException {
        System.out.println("Sample\tTime (ms)");
        // Inputs
        String[] samples = new String[]{LOG4J, HTMLUNIT, POOKA, MEGAMEK};
        for (String sample : samples) {
            File exclusions = new File("Java60RegressionExclusions.txt");

            // Basic Variables
            AnalysisScope scope = makeAnalysisScope(sample, exclusions);
            IClassHierarchy cha = makeIClassHierarchy(scope);
            AnalysisOptions options = makeAnalysisOptions(scope, cha);
            AnalysisCache cache = makeAnalysisCache();

//            // WALA 0-1-CFA
//            computeCallGraph(sample, Util.makeZeroOneCFABuilder(JAVA, options, cache, cha, scope), options);
//            // SALSA 0-1-CFA
            computeCallGraph(sample, SalsaZeroXCallGraphBuilder.make(scope, options, cache, cha, new PointerAnalysisPolicy(ZeroXCFA, 1)), options);

            // WALA 1-CFA
//            computeCallGraph(sample, Util.makeNCFABuilder(1, options, cache, cha, scope), options);
            // SALSA 1-CFA
            computeCallGraph(sample, SalsaNCFACallGraphBuilder.make(scope, options, cache, cha, 1, new PointerAnalysisPolicy(nCFA, 1)), options);

//            cg.stream().filter(n -> n.getMethod() instanceof MethodModel).collect(Collectors.toList());
//            new ProjectAnalysisViewer(cg, null, false).setTitle(sample);
        }


    }


    private static class CustomMonitor implements MonitorUtil.IProgressMonitor {
        private boolean isCanceled = false;

        @Override
        public void beginTask(String s, int i) {
            System.out.println("begin task " + s + " / i = " + i);
        }

        @Override
        public void subTask(String s) {
            System.out.println("sub task " + s);
        }

        @Override
        public void cancel() {
            System.out.println("cancel");
            isCanceled = true;
        }

        @Override
        public boolean isCanceled() {
            return isCanceled;
        }

        @Override
        public void done() {
            System.out.println("done");
        }

        @Override
        public void worked(int i) {
            System.out.println("worked i = " + i);
        }

        @Override
        public String getCancelMessage() {
            return "Some shit happened?";
        }
    }
//<editor-fold desc="Basic Variable Construction">

    public static AnalysisScope makeAnalysisScope(String sourceFile, File exclusions) throws IOException {
        AnalysisScope scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(
                sourceFile,
                exclusions);
        File parentFolder = new File(sourceFile).getParentFile();
        File libFolder = new File(parentFolder.getAbsolutePath() + "/default-lib");
        for (String jarFile : getJarsInDirectory(libFolder.getAbsolutePath())) {
            scope.addToScope(ClassLoaderReference.Extension, new JarFile(jarFile));
        }
        return scope;
    }
    //NOTE: Code methods below are minor adjustments from the code at:
    //    - com.ibm.wala.core/src/com/ibm/wala/properties/WalaProperties.java

    /**
     * Returns a list of jar files in a given directory
     *
     * @param dir directory to be searched
     * @return
     */
    private static String[] getJarsInDirectory(String dir) {
        File f = new File(dir);
        if (f.exists() && !f.isDirectory()) throw new IllegalArgumentException("Not a directory: " + dir);


        Collection<File> col = FileUtil.listFiles(dir, ".*\\.jar$", true);
        String[] result = new String[col.size()];
        int i = 0;
        for (File jarFile : col) result[i++] = jarFile.getAbsolutePath();

        return result;
    }

    public static IClassHierarchy makeIClassHierarchy(AnalysisScope scope) throws ClassHierarchyException {
        return ClassHierarchyFactory.make(scope);
    }

    public static AnalysisOptions makeAnalysisOptions(AnalysisScope scope, IClassHierarchy cha) {
        Iterable<Entrypoint> entrypoints =
                //Util.makeMainEntrypoints(scope, cha);
                //.makeMainEntrypoints(scope, cha);
                new AllApplicationEntrypoints(scope, cha);
        AnalysisOptions options = new AnalysisOptions();
        options.setEntrypoints(entrypoints);
        options.setReflectionOptions(AnalysisOptions.ReflectionOptions.NONE);

        return options;
    }

    public static AnalysisCache makeAnalysisCache() {
        return new AnalysisCacheImpl(AstIRFactory.makeDefaultFactory());
    }
//</editor-fold>

}