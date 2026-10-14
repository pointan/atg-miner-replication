package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.UnaryMinusSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.UnaryMinusNode;

/**
 * <p>Converts {@link UnaryMinusNode} to {@link UnaryMinusSemantic}</p>
 */
public class UnaryMinusNodeToUnaryMinusSemantic extends AbstractUnaryExpressionNodeToBinaryExprSemantic<UnaryMinusNode, UnaryMinusSemantic>{
	public UnaryMinusNodeToUnaryMinusSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		super(operandProcessor, UnaryMinusSemantic::new, UnaryMinusNode.class::isInstance);
	}
}
