package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.FloatSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.python.FloatNode;

/**
 * <p>{@link FloatNode} to {@link FloatSemantic}</p>
 */
public class FloatNodeToFloatSemantic implements NodeToSemantic<FloatNode, FloatSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

    public FloatNodeToFloatSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        this.operandProcessor = operandProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof FloatNode;
    }

    @Override
    public FloatSemantic process(FloatNode node) {
        return new FloatSemantic(node, operandProcessor.process(node.getParam(0)));
    }
}
