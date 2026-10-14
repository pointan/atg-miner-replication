package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.EqualSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.EqualNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Converts an {@link EqualNode} to an {@link EqualSemantic}</p>
 */
public class EqualNodeToEqualSemantic extends AbstractBinaryExpressionNodeToBinaryExprSemantic<EqualNode, EqualSemantic> {

	public EqualNodeToEqualSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		super(operandProcessor, EqualSemantic::new, EqualNode.class::isInstance);
	}

}
