package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.AndSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.AndNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Converts {@link AndNode} to {@link AndSemantic}</p>
 */
public class AndNodeToAndSemantic extends AbstractBinaryExpressionNodeToBinaryExprSemantic<AndNode, AndSemantic> {

	public AndNodeToAndSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		super(operandProcessor, AndSemantic::new, AndNode.class::isInstance);
	}
}
