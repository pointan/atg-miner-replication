package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Semantic class that represents a static value</p>
 */
public class ConstSemantic extends BaseSemantic{

	private final Object value;

	public ConstSemantic(InterpreterNode node, Object value) {
		super(node);
		this.value = value;
	}

	@Override
	public String toString() {
		if (value instanceof Character) {
			return "'" + value +  "'";
		}
		if (value instanceof String) {
			return "\"" + value + "\"";
		}
		return value.toString();
	}

	public Object getValue() {
		return value;
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return List.of();
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new ConstSemantic(getBasedOn(), value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConstSemantic that = (ConstSemantic) o;
		return Objects.equals(getValue(), that.getValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getValue());
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitConstSemantic(this);
	}
}
