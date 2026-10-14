package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.InSetSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InSetNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * Converts {@link InSetNode} to {@link InSetSemantic} .
 */
public class InSetNodeToInSetSemantic implements NodeToSemantic<InSetNode, InSetSemantic> {

	private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

	public InSetNodeToInSetSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		this.operandProcessor = operandProcessor;
	}

	@Override
	public boolean canProcess(InterpreterNode node) {
		return node instanceof InSetNode;
	}

	@Override
	public InSetSemantic process(InSetNode node) {
		return new InSetSemantic(node, operandProcessor.process(node.getLeft()), operandProcessor.process(node.getRight()));
	}
}
