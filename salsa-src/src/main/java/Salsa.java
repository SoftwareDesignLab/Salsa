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

import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.util.MonitorUtil;
import edu.rit.se.design.callgraph.analysis.PointerAnalysisPolicy;
import edu.rit.se.design.callgraph.analysis.salsa.SalsaSSAPropagationCallGraphBuilder;
import edu.rit.se.design.callgraph.analysis.salsa.SalsaZeroXCallGraphBuilder;
import edu.rit.se.design.callgraph.model.MethodModel;
import edu.rit.se.design.callgraph.serializer.DotCallGraphSerializer;
import edu.rit.se.design.callgraph.serializer.JsonJcgSerializer;
import edu.rit.se.design.dodo.utils.debug.DodoLogger;
import edu.rit.se.design.dodo.utils.viz.ProjectAnalysisViewer;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.rit.se.design.callgraph.analysis.PointerAnalysisPolicy.PolicyType.*;
import static edu.rit.se.design.callgraph.util.AnalysisUtils.*;

/**
 * Command Line interface
 * @author Joanna C. S. Santos (jds5109@rit.edu)
 */
public class Salsa {

    // argument names
    public static final String PRINT_MODELS = "print-models";
    public static final String VIEW_UI = "view-ui";
    public static final String EXCLUSIONS = "exclusions";
    public static final String FORMAT = "format";
    public static final String OUTPUT = "output";
    public static final String JAR = "jar";
    public static final String ANALYSIS = "analysis"; // possible values: x-CFA, 0-x-CFA, or 0-x-Container-CFA
    // default values for args
    public static final String DEFAULT_EXCLUSIONS_FILE = "exclusions.txt";
    // for parsing the PA algorithm
    private static final Pattern p = Pattern.compile("((\\d+)-CFA)|(0-(\\d+)-CFA)|(0-(\\d+)-Container-CFA)");

    public static void main( String[] args) throws IOException, ClassHierarchyException, CallGraphBuilderCancelException {
        Logger logger = DodoLogger.getLogger(Salsa.class, false);

        CommandLine cmd = setUpCommandLine(Salsa.class, args);
        String jarFilePath = cmd.getOptionValue(JAR);
        String format = cmd.getOptionValue(FORMAT);
        File outputFile = new File(cmd.getOptionValue(OUTPUT));
        String exclusions = cmd.hasOption(EXCLUSIONS) ? cmd.getOptionValue(EXCLUSIONS) : Salsa.class.getClassLoader().getResource(DEFAULT_EXCLUSIONS_FILE).toString();
        boolean showUi = cmd.hasOption(VIEW_UI);
        boolean printMethods = cmd.hasOption(PRINT_MODELS);
        PointerAnalysisPolicy paPolicy = parsePointerAnalysisPolicy(cmd.getOptionValue(ANALYSIS));


        long start = System.currentTimeMillis();
        logger.info("Starting analysis");
        logger.info("\tJar: " + jarFilePath);
        logger.info("\tOutput: " + outputFile);
        logger.info("\tFormat: " + format);
        logger.info("\t2nd Policy: " + paPolicy);
//        logger.info("\tAlgorithm: " + useTaintBasedAlgorithm);


        // Basic Variables
        File exclusionFile = new File(exclusions);
        AnalysisScope scope = makeAnalysisScope(jarFilePath, exclusionFile);
        IClassHierarchy cha = makeIClassHierarchy(scope);
        AnalysisOptions options = makeAnalysisOptions(scope, cha);
        AnalysisCache cache = makeAnalysisCache();

        // call graph construction
        CallGraphBuilder builder = SalsaZeroXCallGraphBuilder.make(scope, options, cache, cha, paPolicy);
        CallGraph cg = (CallGraph) builder.makeCallGraph(options, new CustomMonitor(logger));
        long end = System.currentTimeMillis();

        logger.info("Call graph computed in " + ((end - start) / 1000L) + " seconds");

        // Visualize call graph in Java Swing
        if (showUi) {
            Set<PointerKey> deserializedObjects = 
                    ((SalsaSSAPropagationCallGraphBuilder) builder).getDeserializedObjects();
            new ProjectAnalysisViewer(cg, deserializedObjects, false).setTitle(jarFilePath);
        }
        // Prints synthetic methods created by Salsa
        if (printMethods) {
            for (CGNode cgNode : cg) {
                if (cgNode.getMethod() instanceof MethodModel) {
                    System.out.println(cgNode);
                    String[] lines = cgNode.getIR().toString().split("Instructions:\n")[1].split("\n");
                    for (String line : lines) {
                        if (line.startsWith("BB")) continue;
                        System.out.println("\t" + line);
                    }
                }
            }
        }


        // saving results
        switch (OutputFormat.valueOf(format.toUpperCase())) {
            case DOT:
                new DotCallGraphSerializer().save(cg, outputFile);
                break;
            case JSON:
                new JsonJcgSerializer().save(cg, outputFile);
        }
    }


    private static PointerAnalysisPolicy parsePointerAnalysisPolicy(String analysis) {
        Matcher matcher = p.matcher(analysis.trim());
        if (!matcher.find()) throw new IllegalArgumentException("Unknown analysis policy " + analysis);


        if (analysis.matches("\\d+-CFA"))
            return new PointerAnalysisPolicy(nCFA, Integer.valueOf(matcher.group(2)));

        if (analysis.matches("0-\\d+-CFA"))
            return new PointerAnalysisPolicy(ZeroXCFA, Integer.valueOf(matcher.group(4)));

        if (analysis.matches("0-\\d+-Containter-CFA"))
            return new PointerAnalysisPolicy(ZeroXContainerCFA, Integer.valueOf(matcher.group(6)));


        throw new IllegalArgumentException("Unknown analysis policy " + analysis);

    }

    /**
     * Set ups the options for the CLI
     *
     * @param args program arguments
     * @return a {@link CommandLine} instance for retrieving the program args
     */
    private static CommandLine setUpCommandLine(Class cliClass, String[] args) {


        Option jar = new Option("j", JAR, true, "Path to the project's JAR file");
        jar.setRequired(true);

        Option output = new Option(OUTPUT.substring(0, 1), OUTPUT, true, "Path to the output file with the serialized call graph");
        output.setRequired(true);

        Option formatOpt = new Option(FORMAT.substring(0, 1), FORMAT, true, "Output format (possible values: json, csv [default = json])");
        formatOpt.setType(OutputFormat.class);
        formatOpt.setRequired(true);


        Option exclusionFile = new Option(EXCLUSIONS.substring(0, 1), EXCLUSIONS, true, "Path to the exclusions file");
        exclusionFile.setRequired(false);


        Option viewUi = new Option(null, VIEW_UI, false, "Shows call graph in a Java Swing UI");
        viewUi.setRequired(false);

        Option printMethodModels = new Option(null, PRINT_MODELS, false, "Prints to the console all the synthetic methods created");
        printMethodModels.setRequired(false);


        Option paPolicy = new Option(ANALYSIS.substring(0, 1), ANALYSIS, true, "Pointer analysis choice (n-CFA, 0-n-CFA, 0-n-Container-CFA)");
        paPolicy.setRequired(true);


        DefaultParser parser = new DefaultParser();
        Options options = new Options();

        options.addOption(jar);
        options.addOption(output);
        options.addOption(formatOpt);
        options.addOption(viewUi);
        options.addOption(printMethodModels);
        options.addOption(exclusionFile);
        options.addOption(paPolicy);


        try {
            if (args.length == 0) showUsageAndExit(cliClass, options);
            return parser.parse(options, args);
        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            showUsageAndExit(cliClass, options);
            return null;
        }
    }

    /**
     * Simply prints a help menu for using this command line interface.
     *
     * @param options the options used to set up the command line
     */
    private static void showUsageAndExit(Class cliClass, Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(cliClass.getSimpleName(), options);
        System.exit(-1);
    }

    public enum OutputFormat {
        JSON, DOT
    }

    /**
     * Simple monitor that prints out in the console what is going on.
     */
    private static class CustomMonitor implements MonitorUtil.IProgressMonitor {
        private final Logger logger;
        private boolean isCanceled = false;
        private int taskNo;

        private CustomMonitor(Logger logger) {
            this.logger = logger;
        }


        @Override
        public void beginTask(String s, int i) {
            logger.info("BEGIN TASK #" + i + ": " + s);
            this.taskNo = i;
        }

        @Override
        public void subTask(String s) {
            logger.info("SUBTASK " + s);
        }

        @Override
        public void cancel() {
            logger.info("CANCEL");
            isCanceled = true;
        }

        @Override
        public boolean isCanceled() {
            return isCanceled;
        }

        @Override
        public void done() {
            logger.info("DONE");
        }

        @Override
        public void worked(int i) {
            logger.info(String.format("\tWORKED %d.%d", taskNo, i));
        }

        @Override
        public String getCancelMessage() {
            return "Error happened";
        }
    }


}
