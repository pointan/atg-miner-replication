package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.TruncateSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.func.AdeleNodeFactory.TruncNode;

/**
 * <p>
 * Converts {@link TruncNode} to {@link TruncateSemantic}
 * </p>
 */
public class TruncNodeToTruncateSemantic implements NodeToSemantic<TruncNode, TruncateSemantic> {

	private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

	public TruncNodeToTruncateSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		this.operandProcessor = operandProcessor;
	}

	@Override
	public boolean canProcess(InterpreterNode node) {
		return node instanceof TruncNode;
	}

	@Override
	public TruncateSemantic process(TruncNode node) {
		return new TruncateSemantic(node, operandProcessor.process(node.getParam(0)));
	}
}
