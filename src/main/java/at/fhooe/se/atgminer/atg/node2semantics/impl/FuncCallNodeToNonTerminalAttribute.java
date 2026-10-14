package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.FunctionCallPlaceholderSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.FuncCallNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Converts {@link FuncCallNode} to {@link FunctionCallPlaceholderSemantic}</p>
 */
public class FuncCallNodeToNonTerminalAttribute implements NodeToSemantic<FuncCallNode, FunctionCallPlaceholderSemantic> {

	@Override
	public boolean canProcess(InterpreterNode node) {
		return node instanceof FuncCallNode;
	}

	@Override
	public FunctionCallPlaceholderSemantic process(FuncCallNode node) {
		return new FunctionCallPlaceholderSemantic(node, node.getFuncName());
	}
}
