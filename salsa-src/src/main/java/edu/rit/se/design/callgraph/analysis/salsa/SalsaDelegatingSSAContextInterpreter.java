package edu.rit.se.design.callgraph.analysis.salsa;


import com.ibm.wala.cfg.ControlFlowGraph;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.propagation.SSAContextInterpreter;
import com.ibm.wala.ssa.*;
import com.ibm.wala.types.FieldReference;

import java.util.Iterator;

/**
 * Interprets a method and return information needed for CFA.
 * This uses a delegation pattern. If node is tainted, delegates to the A context interpreter, otherwise, it delegates to B.
 *
 * @author Reese A. Jones (raj8065@rit.edu)
 * @author Joanna C. S. Santos (jds5109@rit.edu)
 */

public class SalsaDelegatingSSAContextInterpreter implements SSAContextInterpreter {

    SalsaSSAPropagationCallGraphBuilder builder;
    /**
     * reference to the interpreter of the underlying PA policy
     */
    private SSAContextInterpreter normalInterpreter;
    /**
     * interpreter tailored for our model nodes (synthetic)
     */
    private SSAContextInterpreter modelNodesInterpreter;

    /**
     * @param builder               callgraph builder
     * @param normalInterpreter     context interpreter for non-model nodes
     * @param modelNodesInterpreter context interpreter for model nodes
     */
    public SalsaDelegatingSSAContextInterpreter(
            SalsaSSAPropagationCallGraphBuilder builder,
            SSAContextInterpreter normalInterpreter,
            SSAContextInterpreter modelNodesInterpreter) {

        if (normalInterpreter == null)
            throw new IllegalArgumentException("Untainted ContextInterpreter for ContextInterpreter delegation is null");
        if (modelNodesInterpreter == null)
            throw new IllegalArgumentException("Tainted ContextInterpreter for ContextInterpreter delegation is null");
        if (builder == null)
            throw new IllegalArgumentException("Builder for ContextInterpreter delegation is null");

        this.builder = builder;
        this.normalInterpreter = normalInterpreter;
        this.modelNodesInterpreter = modelNodesInterpreter;
    }

    @Override
    public Iterator<NewSiteReference> iterateNewSites(CGNode node) {
//        System.out.println(node);
        if (!builder.isSyntheticModel(node))
            return normalInterpreter.iterateNewSites(node);

        return modelNodesInterpreter.iterateNewSites(node);
    }

    @Override
    public Iterator<FieldReference> iterateFieldsRead(CGNode node) {
        if (!builder.isSyntheticModel(node))
            return normalInterpreter.iterateFieldsRead(node);

        return modelNodesInterpreter.iterateFieldsRead(node);
    }

    @Override
    public Iterator<FieldReference> iterateFieldsWritten(CGNode node) {
        if (!builder.isSyntheticModel(node))
            return normalInterpreter.iterateFieldsWritten(node);

        return modelNodesInterpreter.iterateFieldsWritten(node);
    }

    @Override
    public boolean recordFactoryType(CGNode node, IClass klass) {
        if (!builder.isSyntheticModel(node))
            return normalInterpreter.recordFactoryType(node, klass);

        return modelNodesInterpreter.recordFactoryType(node, klass);
    }

    @Override
    public boolean understands(CGNode node) {
        if (!builder.isSyntheticModel(node))
            return normalInterpreter.understands(node);

        return modelNodesInterpreter.understands(node);
    }

    @Override
    public Iterator<CallSiteReference> iterateCallSites(CGNode node) {
        if (!builder.isSyntheticModel(node))
            return normalInterpreter.iterateCallSites(node);

        return modelNodesInterpreter.iterateCallSites(node);
    }

    @Override
    public IR getIR(CGNode node) {
        if (!builder.isSyntheticModel(node))
            return normalInterpreter.getIR(node);

        return modelNodesInterpreter.getIR(node);
    }

    @Override
    public IRView getIRView(CGNode node) {
        if (!builder.isSyntheticModel(node))
            return normalInterpreter.getIRView(node);

        return modelNodesInterpreter.getIRView(node);
    }

    @Override
    public DefUse getDU(CGNode node) {
        if (!builder.isSyntheticModel(node))
            return normalInterpreter.getDU(node);

        return modelNodesInterpreter.getDU(node);
    }

    @Override
    public int getNumberOfStatements(CGNode node) {
        if (!builder.isSyntheticModel(node))
            return normalInterpreter.getNumberOfStatements(node);

        return modelNodesInterpreter.getNumberOfStatements(node);
    }

    @Override
    public ControlFlowGraph<SSAInstruction, ISSABasicBlock> getCFG(CGNode n) {
        if (!builder.isSyntheticModel(n))
            return normalInterpreter.getCFG(n);

        return modelNodesInterpreter.getCFG(n);
    }
}
