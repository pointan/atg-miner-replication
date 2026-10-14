package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Semantic class that represents the Pascal {@code Length} function on strings.
 */
public class StrLenSemantic extends FunctionSemantic {

	private final BaseSemantic parameter;

	public StrLenSemantic(InterpreterNode node, BaseSemantic parameter) {
		super(node);
		this.parameter = Objects.requireNonNull(parameter);
	}

	@Override
	public String toString() {
		return "StrLen(" + parameter + ")";
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
		return new StrLenSemantic(getBasedOn(), getParameter().replace(replacement));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		StrLenSemantic that = (StrLenSemantic) o;
		return getParameter().equals(that.getParameter());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getParameter());
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitStrLenSemantic(this);
	}
}
