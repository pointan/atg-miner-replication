package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.MultiplySemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.MultiplyNode;

/**
 * <p>Converts {@link MultiplyNode} to {@link MultiplySemantic}</p>
 */
public class MultiplyNodeToMultiplySemantic extends AbstractBinaryExpressionNodeToBinaryExprSemantic<MultiplyNode, MultiplySemantic> {

	public MultiplyNodeToMultiplySemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		super(operandProcessor, MultiplySemantic::new, MultiplyNode.class::isInstance);
	}
}
