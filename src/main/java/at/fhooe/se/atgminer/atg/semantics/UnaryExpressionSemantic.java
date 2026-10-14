package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Base class for Unary Operations</p>
 */
public abstract class UnaryExpressionSemantic extends BaseSemantic {
	private final BaseSemantic operand;

	public UnaryExpressionSemantic(InterpreterNode node, BaseSemantic operand) {
		super(node);
		this.operand = Objects.requireNonNull(operand);
	}

	public BaseSemantic getOperand() {
		return operand;
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return List.of(operand);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UnaryExpressionSemantic that = (UnaryExpressionSemantic) o;
		return getOperand().equals(that.getOperand());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getOperand());
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitUnaryExpressionSemantic(this);
	}
}
