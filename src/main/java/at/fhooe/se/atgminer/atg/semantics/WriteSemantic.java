package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class WriteSemantic extends BaseSemantic {

	private final List<BaseSemantic> params;

	public WriteSemantic(InterpreterNode node, List<BaseSemantic> params) {
		super(node);
		this.params = params;
	}

	@Override
	public String toString() {
		StringJoiner str = new StringJoiner(", ", "Write(", ")");
		params.forEach((e) -> str.add(e.toString()));
		return str.toString();
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return params;
	}

	@Override
	public int hashCode() {
		return Objects.hash(params);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WriteSemantic other = (WriteSemantic) obj;
		return Objects.equals(params, other.params);
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new WriteSemantic(getBasedOn(), params.stream().map(p -> p.replace(replacement)).collect(Collectors.toList()));
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitWriteSemantic(this);
	}

}
