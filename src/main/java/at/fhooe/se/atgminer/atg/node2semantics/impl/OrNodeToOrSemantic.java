package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.OrSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.OrNode;

/**
 * <p>Converts {@link OrNode} to {@link OrSemantic}</p>
 */
public class OrNodeToOrSemantic extends AbstractBinaryExpressionNodeToBinaryExprSemantic<OrNode, OrSemantic> {

	public OrNodeToOrSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		super(operandProcessor, OrSemantic::new, OrNode.class::isInstance);
	}
}
