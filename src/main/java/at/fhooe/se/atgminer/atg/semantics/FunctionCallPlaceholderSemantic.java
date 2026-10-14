package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Placeholder for a function call.</p>
 */
public class FunctionCallPlaceholderSemantic extends BaseSemantic {
	private final String funcName;

	public FunctionCallPlaceholderSemantic(InterpreterNode node, String funcName) {
		super(node);
		this.funcName = Objects.requireNonNull(funcName);
	}

	public String getFuncName() {
		return funcName;
	}

	@Override
	public String toString() {
		return "result(" + funcName + ")";
	}


	@Override
	public List<BaseSemantic> getChildren() {
		return List.of();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FunctionCallPlaceholderSemantic that = (FunctionCallPlaceholderSemantic) o;
		return getFuncName().equals(that.getFuncName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFuncName());
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new FunctionCallPlaceholderSemantic(getBasedOn(), funcName);
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitFunctionCallPlaceholderSemantic(this);
	}
}
