package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.UnaryExpressionSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.UnaryExpressionNode;

import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * <p>Abstraction class that processes sub classes of {@link UnaryExpressionNode}s and create sub classes {@link UnaryExpressionSemantic}s.</p>
 */
public class AbstractUnaryExpressionNodeToBinaryExprSemantic<N extends UnaryExpressionNode, S extends UnaryExpressionSemantic> implements NodeToSemantic<N, S> {

	private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;
	private final BiFunction<InterpreterNode, BaseSemantic, S> creator;
	private final Predicate<InterpreterNode> condition;

	AbstractUnaryExpressionNodeToBinaryExprSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor, BiFunction<InterpreterNode, BaseSemantic, S> creator, Predicate<InterpreterNode> condition) {
		this.operandProcessor = operandProcessor;
		this.creator = creator;
		this.condition = condition;
	}

	@Override
	public boolean canProcess(InterpreterNode node) {
		return condition.test(node);
	}

	@Override
	public S process(N node) {
		return creator.apply(node,operandProcessor.process(node.getOperand()));
	}
}
