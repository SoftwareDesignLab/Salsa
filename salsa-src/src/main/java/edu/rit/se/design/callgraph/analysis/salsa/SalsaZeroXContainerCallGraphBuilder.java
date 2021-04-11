package edu.rit.se.design.callgraph.analysis.salsa;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.propagation.PointerKeyFactory;
import com.ibm.wala.ipa.callgraph.propagation.SSAContextInterpreter;
import com.ibm.wala.ipa.callgraph.propagation.cfa.DefaultPointerKeyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import edu.rit.se.design.callgraph.analysis.PointerAnalysisPolicy;

import static edu.rit.se.design.callgraph.analysis.PointerAnalysisPolicy.PolicyType.ZeroXCFA;


/**
 * @author Reese A. Jones (raj8065@rit.edu)
 * @author Joanna C. S. Santos (jds5109@rit.edu)
 */
public class SalsaZeroXContainerCallGraphBuilder extends SalsaZeroXCallGraphBuilder {

    protected SalsaZeroXContainerCallGraphBuilder(IMethod abstractRootMethod, AnalysisOptions options,
                                                  IAnalysisCacheView cache, PointerKeyFactory pointerKeyFactory, ContextSelector appContextSelector,
                                                  int primaryPolicyVal, ContextSelector delegatedAppContextSelector,
                                                  SSAContextInterpreter appContextInterpreter, SSAContextInterpreter delegatedAppContextInterpreter,
                                                  PointerAnalysisPolicy type) {
        super(abstractRootMethod, options, cache, pointerKeyFactory,
                appContextSelector, delegatedAppContextSelector,
                appContextInterpreter, delegatedAppContextInterpreter,
                new PointerAnalysisPolicy(ZeroXCFA, primaryPolicyVal), type);
    }

    @SuppressWarnings("rawtypes")
    public static CallGraphBuilder make(
            AnalysisScope scope,
            AnalysisOptions options,
            IAnalysisCacheView cache,
            IClassHierarchy cha,
            PointerAnalysisPolicy type) {
        return new SalsaZeroXContainerCallGraphBuilder(
                Language.JAVA.getFakeRootMethod(cha, options, cache),
                options, cache,
                new DefaultPointerKeyFactory(),
                null, type.policyNumber,
                null,
                null, null,
                type);
    }

    @Override
    protected ContextSelector makePrimaryContextSelector(ContextSelector appContextSelector) {
        return makeZeroXContainerContextSelector(appContextSelector, true);
    }

}
