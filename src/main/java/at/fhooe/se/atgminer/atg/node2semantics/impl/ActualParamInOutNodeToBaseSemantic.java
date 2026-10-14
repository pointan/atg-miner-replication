package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ActualParamInOutNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Converts {@link ActualParamInOutNode} to {@link BaseSemantic}</p>
 */
public class ActualParamInOutNodeToBaseSemantic implements NodeToSemantic<ActualParamInOutNode, BaseSemantic> {

	private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

	public ActualParamInOutNodeToBaseSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		this.operandProcessor = operandProcessor;
	}

	@Override
	public boolean canProcess(InterpreterNode node) {
		return node instanceof ActualParamInOutNode;
	}

	@Override
	public BaseSemantic process(ActualParamInOutNode node) {
		return operandProcessor.process(node.getExpression());
	}
}
