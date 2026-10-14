package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.SubtractSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.SubtractNode;

/**
 * <p>Converts {@link SubtractNode} to {@link SubtractSemantic}</p>
 */
public class SubtractNodeToSubtractSemantic extends AbstractBinaryExpressionNodeToBinaryExprSemantic<SubtractNode, SubtractSemantic> {

	public SubtractNodeToSubtractSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		super(operandProcessor, SubtractSemantic::new, SubtractNode.class::isInstance);
	}
}
