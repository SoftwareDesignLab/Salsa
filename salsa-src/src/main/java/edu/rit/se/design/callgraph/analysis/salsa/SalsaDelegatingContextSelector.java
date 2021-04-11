package edu.rit.se.design.callgraph.analysis.salsa;


import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Context;
import com.ibm.wala.ipa.callgraph.ContextSelector;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.util.intset.IntSet;


/**
 * It controls the context selection policy via delegation.
 *
 * @author Reese A. Jones (raj8065@rit.edu)
 * @author Joanna C. S. Santos (jds5109@rit.edu)
 * @see ContextSelector
 */
public class SalsaDelegatingContextSelector implements ContextSelector {

    private ContextSelector A;
    private ContextSelector B;
    private SalsaSSAPropagationCallGraphBuilder builder;

    public SalsaDelegatingContextSelector(
            SalsaSSAPropagationCallGraphBuilder builder,
            ContextSelector A,
            ContextSelector B) {

        if (A == null)
            throw new IllegalArgumentException("Untainted ContextInterpreter for ContextInterpreter delegation is null");
        if (B == null)
            throw new IllegalArgumentException("Tainted ContextInterpreter for ContextInterpreter delegation is null");
        if (builder == null)
            throw new IllegalArgumentException("Builder for ContextInterpreter delegation is null");

        this.builder = builder;
        this.A = A;
        this.B = B;
    }

    /**
     * Delegation-based implementation.
     * If the caller is
     *
     * @param caller           the node making the invocation
     * @param site             callsite
     * @param callee           the node corresponding to the invocation target
     * @param actualParameters the passes instance types
     * @return the {@link Context} to be used in this call.
     */
    @Override
    public Context getCalleeTarget(CGNode caller, CallSiteReference site, IMethod callee, InstanceKey[] actualParameters) {
        if (!builder.isSyntheticModel(caller))
            return A.getCalleeTarget(caller, site, callee, actualParameters);

        return B.getCalleeTarget(caller, site, callee, actualParameters);
    }

    @Override
    public IntSet getRelevantParameters(CGNode caller, CallSiteReference site) {
        if (!builder.isSyntheticModel(caller))
            return A.getRelevantParameters(caller, site);

        return B.getRelevantParameters(caller, site);
    }
}
