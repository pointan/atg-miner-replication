package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Semantic class that represents the {@code Trunc} function.</p>
 */
public class TruncateSemantic extends FunctionSemantic {

	private final BaseSemantic parameter;

	public TruncateSemantic(InterpreterNode node, BaseSemantic parameter) {
		super(node);
		this.parameter = Objects.requireNonNull(parameter);
	}

	@Override
	public String toString() {
		return "Trunc("+parameter+")";
	}

	public BaseSemantic getParameter() {
		return parameter;
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return List.of(parameter);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TruncateSemantic that = (TruncateSemantic) o;
		return getParameter().equals(that.getParameter());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getParameter());
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new TruncateSemantic(getBasedOn(), getParameter().replace(replacement));
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitTruncateSemantic(this);
	}
}
