package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ResultPlaceholderSemantic extends BaseSemantic {

	private final String funcName;

	public ResultPlaceholderSemantic(InterpreterNode node, String funcName) {
		super(node);
		this.funcName = funcName;
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
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new ResultPlaceholderSemantic(getBasedOn(), funcName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ResultPlaceholderSemantic that = (ResultPlaceholderSemantic) o;
		return Objects.equals(funcName, that.funcName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(funcName);
	}

	public String getFunctionName() {
		return funcName;
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitResultPlaceholderSemantic(this);
	}

}
