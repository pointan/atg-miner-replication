package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.StrLenSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.func.StringLengthNode;

/**
 * Converts {@link StringLengthNode} to {@link StrLenSemantic} .
 */
public class StringLengthNodeToStrLenSemantic implements NodeToSemantic<StringLengthNode, BaseSemantic> {

	private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

	public StringLengthNodeToStrLenSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		this.operandProcessor = operandProcessor;
	}

	@Override
	public boolean canProcess(InterpreterNode node) {
		return node instanceof StringLengthNode;
	}

	@Override
	public BaseSemantic process(StringLengthNode node) {
		return new StrLenSemantic(node, operandProcessor.process(node.getParams()[0]));
	}
}
