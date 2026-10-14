package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.Power2Semantic;
import at.fhooe.se.atgminer.atg.semantics.ResultPlaceholderSemantic;
import at.fhooe.se.atgminer.atg.semantics.UnpackingSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.FuncCallNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * Converts {@link FuncCallNode} to {@link ResultPlaceholderSemantic} and
 * handles specific function such as Power of Two ({@code Power2}).
 * </p>
 * This class is part of the concept implementation, version 2.
 */
public class FuncCallNodeToResultPlaceholder implements NodeToSemantic<FuncCallNode, BaseSemantic> {

	private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

	public FuncCallNodeToResultPlaceholder(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		this.operandProcessor = operandProcessor;
	}

	@Override
	public boolean canProcess(InterpreterNode node) {
		return node instanceof FuncCallNode;
	}

	@Override
	public BaseSemantic process(FuncCallNode node) {

		String s = node.toString();
		if (s.contains("Power2")) {
			return new Power2Semantic(node, operandProcessor.process(node.getParams()[0]));
		} else if (node.getFuncName().equals("star")) {
			return new UnpackingSemantic(node, operandProcessor.process(node.getParams()[0]));
		} else {
			return new ResultPlaceholderSemantic(node, node.getFuncName());
		}
	}
}
