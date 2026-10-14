package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReturnPlaceholderSemantic extends BaseSemantic {

	private final BaseSemantic returnValue;

	public ReturnPlaceholderSemantic(InterpreterNode node, BaseSemantic returnValue) {
		super(node);
		this.returnValue = Objects.requireNonNull(returnValue);
	}

	@Override
	public String toString() {
		return "return " + returnValue;
	}

	public BaseSemantic getResultValue() {
		return returnValue;
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return List.of(returnValue);
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new ReturnPlaceholderSemantic(getBasedOn(), getResultValue().replace(replacement));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ReturnPlaceholderSemantic that = (ReturnPlaceholderSemantic) o;
		return returnValue.equals(that.returnValue);
	}

	@Override
	public int hashCode() {
		return Objects.hash(returnValue);
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitReturnPlaceholderSemantic(this);
	}
}
