package edu.rit.se.design.callgraph.evaluation.utils;


import edu.rit.se.design.callgraph.TestUtilities;

public class XCorpusTestCases {


    public static final String BATIK_TC = TestUtilities.TC_ROOT_FOLDER + "batik-testcases.jar";
    public static final String CASTOR_TC = TestUtilities.TC_ROOT_FOLDER + "castor-testcases.jar";
    public static final String HTMLUNIT_TC = TestUtilities.TC_ROOT_FOLDER + "htmlunit-testcases.jar";
    public static final String JAMES_TC = TestUtilities.TC_ROOT_FOLDER + "james-testcases.jar";
    public static final String LOG4J_TC = TestUtilities.TC_ROOT_FOLDER + "log4j-testcases.jar";
    public static final String COMMONS_COLLECTION_TC = TestUtilities.TC_ROOT_FOLDER + "commons-collections-testcases.jar";
    public static final String JEDIT_TC = TestUtilities.TC_ROOT_FOLDER + "jedit-testcases.jar";
    public static final String JPF_TC = TestUtilities.TC_ROOT_FOLDER + "jpf-testcases.jar";


    // Args

    public static String[] LOG4J_TC_ARGS = new String[]{
            "-j", LOG4J_TC,
            "-o", "./target/log4j-testcases.dot",
            "-f", "dot",
            "--analysis", "1-CFA",
            "--view-ui",
//            "--taint",
            "--print-models",
    };


    public static String[] JPF_TC_ARGS = new String[]{
            "-j", JPF_TC,
            "-o", "./target/jpf-testcases.dot",
            "-f", "dot",
            "--analysis", "1-CFA",
            "--view-ui",
            "--taint",
            "--print-models",
    };


}
