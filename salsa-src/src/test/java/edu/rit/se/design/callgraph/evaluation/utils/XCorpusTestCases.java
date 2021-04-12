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
