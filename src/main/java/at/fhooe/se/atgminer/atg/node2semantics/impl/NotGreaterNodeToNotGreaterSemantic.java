package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.NotGreaterSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.NotGreaterNode;

/**
 * Converts {@link NotGreaterNode} objects to {@link NotGreaterSemantic}
 * objects.
 */
public class NotGreaterNodeToNotGreaterSemantic extends AbstractBinaryExpressionNodeToBinaryExprSemantic<NotGreaterNode, NotGreaterSemantic> {

	public NotGreaterNodeToNotGreaterSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		super(operandProcessor, NotGreaterSemantic::new, NotGreaterNode.class::isInstance);
	}

}
