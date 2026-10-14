package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.ChrSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.pascal.ChrNode;

/**
 * <p>Converts {@link ChrNode} to {@link ChrSemantic}</p>
 */
public class ChrNodeToChrSemantic implements NodeToSemantic<ChrNode, ChrSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

    public ChrNodeToChrSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        this.operandProcessor = operandProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof ChrNode;
    }

    @Override
    public ChrSemantic process(ChrNode node) {
        return new ChrSemantic(node, operandProcessor.process(node.getParam(0)));
    }
}
