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

package edu.rit.se.design.callgraph.util;

import com.ibm.wala.cast.ir.ssa.AstIRFactory;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for creating required data structures for call graph construction.
 * {@link IClassHierarchy}, {@link AnalysisScope}, {@link AnalysisOptions}
 *
 * @author Joanna C. S. Santos (jds5109@rit.edu)
 */
public class AnalysisUtils {
    public static AnalysisScope makeAnalysisScope(String sourceFile, File exclusions) throws IOException {
        return AnalysisScopeReader.makeJavaBinaryAnalysisScope(
                sourceFile,
                exclusions);
    }


    public static IClassHierarchy makeIClassHierarchy(AnalysisScope scope) throws ClassHierarchyException {
        return ClassHierarchyFactory.make(scope);
    }

    public static AnalysisOptions makeAnalysisOptions(AnalysisScope scope, IClassHierarchy cha) {
        return makeAnalysisOptions(scope, cha, false);
    }


    public static AnalysisOptions makeAnalysisOptions(AnalysisScope scope, IClassHierarchy cha, boolean enableReflection) {
        Iterable<Entrypoint> entrypoints = Util.makeMainEntrypoints(scope, cha);

        AnalysisOptions options = new AnalysisOptions();
        options.setEntrypoints(entrypoints);
        if (enableReflection)
            options.setReflectionOptions(AnalysisOptions.ReflectionOptions.FULL);
        else
            options.setReflectionOptions(AnalysisOptions.ReflectionOptions.NONE);
        return options;
    }


    public static AnalysisCache makeAnalysisCache() {
        return new AnalysisCacheImpl(AstIRFactory.makeDefaultFactory());
    }
}
