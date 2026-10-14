package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Semantic class that represents an ord function.</p>
 */
public class OrdSemantic extends FunctionSemantic {

	private final BaseSemantic parameter;

	public OrdSemantic(InterpreterNode node, BaseSemantic parameter) {
		super(node);
		this.parameter = Objects.requireNonNull(parameter);
	}

	@Override
	public String toString() {
		return "Ord("+parameter+")";
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
		OrdSemantic that = (OrdSemantic) o;
		return getParameter().equals(that.getParameter());
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new OrdSemantic(getBasedOn(), getParameter().replace(replacement));
	}

	@Override
	public int hashCode() {
		return Objects.hash(getParameter());
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitOrdSemantic(this);
	}
}
