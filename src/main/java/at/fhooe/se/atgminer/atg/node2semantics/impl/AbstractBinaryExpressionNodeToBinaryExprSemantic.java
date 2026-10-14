package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.BinaryExprSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.BinaryExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.function.Predicate;

/**
 * <p>Abstraction class that processes sub classes of {@link BinaryExpressionNode}s and create sub classes {@link BinaryExprSemantic}s.</p>
 */
public class AbstractBinaryExpressionNodeToBinaryExprSemantic<N extends BinaryExpressionNode, S extends BinaryExprSemantic> implements NodeToSemantic<N, S> {

	private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;
	private final TriFunction<InterpreterNode, BaseSemantic, BaseSemantic, S> creator;
	private final Predicate<InterpreterNode> condition;

	AbstractBinaryExpressionNodeToBinaryExprSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor, TriFunction<InterpreterNode, BaseSemantic, BaseSemantic, S> creator, Predicate<InterpreterNode> condition) {
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
		return creator.apply(node, operandProcessor.process(node.getLeft()), operandProcessor.process(node.getRight()));
	}

	@FunctionalInterface
	public interface TriFunction<T, U, V, R> {
		R apply(T t, U u, V v);
	}
}
