package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>This semantic represents the result of a function</p>
 */
public class ReturnSemantic extends BaseSemantic {
	private final BaseSemantic result;

	public ReturnSemantic(InterpreterNode node, BaseSemantic result) {
		super(node);
		this.result = Objects.requireNonNull(result);
	}

	@Override
	public String toString() {
		return "return(" + result + ")";
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return List.of(result);
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new ReturnSemantic(getBasedOn(), getResult().replace(replacement));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ReturnSemantic that = (ReturnSemantic) o;
		return result.equals(that.result);
	}

	@Override
	public int hashCode() {
		return Objects.hash(result);
	}

	public BaseSemantic getResult() {
		return result;
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitReturnSemantic(this);
	}
}
