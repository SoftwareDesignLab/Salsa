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

import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import edu.rit.se.design.callgraph.evaluation.utils.XCorpusTestCases;

import java.io.IOException;

public class SalsaCliTest {

    public static void main(String[] args) throws ClassHierarchyException, CallGraphBuilderCancelException, IOException {
        Salsa.main(XCorpusTestCases.LOG4J_TC_ARGS);
    }

}