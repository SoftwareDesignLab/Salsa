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

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import edu.rit.se.design.dodo.utils.viz.GraphVisualizer;

import java.io.File;

/**
 * Saves a call graph in dot format.
 *
 * @author Joanna C. S. Santos
 */
public class DotCallGraphSerializer implements ICallGraphSerializer {
    /**
     * Saves a file in DOT format.
     *
     * @param cg         call graph
     * @param outputFile where to save the file
     */
    @Override
    public void save(CallGraph cg, File outputFile) {
        new GraphVisualizer<CGNode>("Call graph",
                GraphVisualizer.getDefaultCgNodeLabeller(),
                GraphVisualizer.getDefaultCgNodeHighlighter(),
                null,
                GraphVisualizer.getDefaultCgNodeRemover(cg))
                .generateVisualGraph(cg, outputFile);
    }
}
