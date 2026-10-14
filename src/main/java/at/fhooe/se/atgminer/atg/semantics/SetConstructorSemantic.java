package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Semantic class that represents the Pascal set constructor.
 */
public class SetConstructorSemantic extends FunctionSemantic {

	private List<BaseSemantic> values;

	public SetConstructorSemantic(InterpreterNode node, List<BaseSemantic> values) {
		super(node);
		this.values = values;
	}

	@Override
	public String toString() {
		StringJoiner str = new StringJoiner(", ", "[", "]");
		values.forEach((e) -> str.add(e.toString()));
		return str.toString();
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return values;
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new SetConstructorSemantic(getBasedOn(), values.stream().map(v -> v.replace(replacement)).collect(Collectors.toList()));
	}

	@Override
	public int hashCode() {
		return Objects.hash(values);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SetConstructorSemantic other = (SetConstructorSemantic) obj;
		return Objects.equals(values, other.values);
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitSetConstructorSemantic(this);
	}

}
