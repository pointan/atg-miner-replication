package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.VariableSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.IdentifierNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Converts {@link IdentifierNode} to {@link VariableSemantic}</p>
 */
public class IdentifierNodeToVariableSemantic implements NodeToSemantic<IdentifierNode, VariableSemantic> {
	@Override
	public boolean canProcess(InterpreterNode node) {
		return node instanceof IdentifierNode;
	}

	@Override
	public VariableSemantic process(IdentifierNode node) {
		return new VariableSemantic(node, node.getIdent(), node.inLocalScope());
	}
}
