package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.SliceSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.python.SliceNode;

/**
 * <p>Converts {@link SliceNode} to {@link SliceSemantic}</p>
 */
public class SliceNodeToSliceSemantic implements NodeToSemantic<SliceNode, SliceSemantic> {
    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

    public SliceNodeToSliceSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        this.operandProcessor = operandProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof SliceNode;
    }

    @Override
    public SliceSemantic process(SliceNode node) {
        return new SliceSemantic(
          node,
          operandProcessor.process(node.getSequenceName()),
          operandProcessor.process(node.getBeginExpr()),
          operandProcessor.process(node.getEndExpr())
        );
    }
}
