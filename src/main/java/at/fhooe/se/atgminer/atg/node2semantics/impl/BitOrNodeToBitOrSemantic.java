package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.BitOrSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.BitOrNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Converts a {@link BitOrNode} to a {@link BitOrSemantic}</p>
 */
public class BitOrNodeToBitOrSemantic extends AbstractBinaryExpressionNodeToBinaryExprSemantic<BitOrNode, BitOrSemantic> {
    public BitOrNodeToBitOrSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        super(operandProcessor, BitOrSemantic::new, BitOrNode.class::isInstance);
    }
}
