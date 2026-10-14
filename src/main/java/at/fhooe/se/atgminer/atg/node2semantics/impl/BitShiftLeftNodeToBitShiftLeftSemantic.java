package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.BitShiftLeftSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.BitShiftLeftNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Converts a {@link BitShiftLeftNode} to a {@link BitShiftLeftSemantic}</p>
 */
public class BitShiftLeftNodeToBitShiftLeftSemantic extends AbstractBinaryExpressionNodeToBinaryExprSemantic<BitShiftLeftNode, BitShiftLeftSemantic> {
    public BitShiftLeftNodeToBitShiftLeftSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        super(operandProcessor, BitShiftLeftSemantic::new, BitShiftLeftNode.class::isInstance);
    }
}
