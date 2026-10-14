package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Objects;

/**
 * <p>Semantic class to represent a binary expression, which consists of a left and a right operand</p>
 */
public abstract class BinaryExprSemantic extends BaseSemantic {
	private final BaseSemantic leftOperand;
	private final BaseSemantic rightOperand;

	public BinaryExprSemantic(InterpreterNode node, BaseSemantic leftOperand, BaseSemantic rightOperand) {
		super(node);
		this.leftOperand = Objects.requireNonNull(leftOperand);
		this.rightOperand = Objects.requireNonNull(rightOperand);
	}

	public BaseSemantic getLeftOperand() {
		return leftOperand;
	}

	public BaseSemantic getRightOperand() {
		return rightOperand;
	}


	@Override
	public List<BaseSemantic> getChildren() {
		return List.of(leftOperand, rightOperand);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BinaryExprSemantic that = (BinaryExprSemantic) o;
		return getLeftOperand().equals(that.getLeftOperand()) && getRightOperand().equals(that.getRightOperand());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getLeftOperand(), getRightOperand());
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitBinaryExprSemantic(this);
	}
}
