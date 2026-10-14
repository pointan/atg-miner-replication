package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.NotEqualSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.NotEqualNode;

/**
 * <p>Converts {@link NotEqualNode} to {@link NotEqualSemantic}</p>
 */
public class NotEqualNodeToNotEqualSemantic extends AbstractBinaryExpressionNodeToBinaryExprSemantic<NotEqualNode, NotEqualSemantic> {

	public NotEqualNodeToNotEqualSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		super(operandProcessor, NotEqualSemantic::new, NotEqualNode.class::isInstance);
	}
}
