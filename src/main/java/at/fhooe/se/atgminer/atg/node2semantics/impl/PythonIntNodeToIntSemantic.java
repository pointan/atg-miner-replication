package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.IntSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.python.PythonIntNode;

/**
 * <p>Converts a {@link PythonIntNode} into a {@link IntSemantic}</p>
 */
public class PythonIntNodeToIntSemantic implements NodeToSemantic<PythonIntNode, IntSemantic>  {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

    public PythonIntNodeToIntSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        this.operandProcessor = operandProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof PythonIntNode;
    }

    @Override
    public IntSemantic process(PythonIntNode node) {
        BaseSemantic param = operandProcessor.process(node.getParam(0));
        BaseSemantic base = null;
        if (node.getParams().length == 2) {
            base = operandProcessor.process(node.getParam(1));
        }
        return new IntSemantic(node, param, base);
    }
}
