package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.ConstSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ValueLiteralNode;

/**
 * <p>Converts a {@link ValueLiteralNode} to a {@link ConstSemantic}</p>
 */
public class ValueLiteralNodeToConstSemantic implements NodeToSemantic<ValueLiteralNode, ConstSemantic> {
	@Override
	public boolean canProcess(InterpreterNode node) {
		return node instanceof ValueLiteralNode;
	}

	@Override
	public ConstSemantic process(ValueLiteralNode node) {
		return new ConstSemantic(node, node.getValue());
	}
}
