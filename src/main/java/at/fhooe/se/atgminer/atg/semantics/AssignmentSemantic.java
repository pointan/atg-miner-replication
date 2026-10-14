package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Semantic class, that represents and assign statement</p>
 */
public class AssignmentSemantic extends BaseSemantic {
	private final BaseSemantic leftHandSide;
	private final BaseSemantic rightHandSide;

	public AssignmentSemantic(InterpreterNode node, BaseSemantic leftHandSide, BaseSemantic rightHandSide) {
		super(node);
		this.leftHandSide = Objects.requireNonNull(leftHandSide);
		this.rightHandSide = Objects.requireNonNull(rightHandSide);
	}

	@Override
	public String toString() {
		return leftHandSide.toString() + " = " + rightHandSide;
	}

	public BaseSemantic getLeftHandSide() {
		return leftHandSide;
	}

	public BaseSemantic getRightHandSide() {
		return rightHandSide;
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return List.of(leftHandSide, rightHandSide);
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new AssignmentSemantic(getBasedOn(), getLeftHandSide().replace(replacement), getRightHandSide().replace(replacement));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AssignmentSemantic that = (AssignmentSemantic) o;
		return getLeftHandSide().equals(that.getLeftHandSide()) && getRightHandSide().equals(that.getRightHandSide());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getLeftHandSide(), getRightHandSide());
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitAssignmentSemantic(this);
	}
}
