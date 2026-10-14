package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Semantic class that represents the power of 2 function.</p>
 */
public class Power2Semantic extends FunctionSemantic {

	private final BaseSemantic parameter;

	public Power2Semantic(InterpreterNode node, BaseSemantic parameter) {
		super(node);
		this.parameter = Objects.requireNonNull(parameter);
	}

	@Override
	public String toString() {
		return "2^("+parameter+")";
	}

	public BaseSemantic getParameter() {
		return parameter;
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return List.of(parameter);
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new Power2Semantic(getBasedOn(), getParameter().replace(replacement));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Power2Semantic that = (Power2Semantic) o;
		return getParameter().equals(that.getParameter());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getParameter());
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitPower2Semantic(this);
	}
}
