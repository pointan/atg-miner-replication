package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.DivideIntSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.DivideIntNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Converts {@link DivideIntNode} to {@link DivideIntSemantic}</p>
 */
public class DivideIntNodeToDivideIntSemantic extends AbstractBinaryExpressionNodeToBinaryExprSemantic<DivideIntNode, DivideIntSemantic> {

	public DivideIntNodeToDivideIntSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		super(operandProcessor, DivideIntSemantic::new, DivideIntNode.class::isInstance);
	}
}
