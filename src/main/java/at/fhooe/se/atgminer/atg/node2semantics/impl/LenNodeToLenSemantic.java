package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.LenSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.python.LenNode;

/**
 * <p>{@link LenNode} to {@link LenSemantic}</p>
 */
public class LenNodeToLenSemantic implements NodeToSemantic<LenNode, LenSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

    public LenNodeToLenSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        this.operandProcessor = operandProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof LenNode;
    }

    @Override
    public LenSemantic process(LenNode node) {
        return new LenSemantic(node, operandProcessor.process(node.getParam(0)));
    }
}
