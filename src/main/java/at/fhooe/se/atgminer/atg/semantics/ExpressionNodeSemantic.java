package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExpressionNodeSemantic extends BaseSemantic {

	private final String exprNodeStr;

	public ExpressionNodeSemantic(InterpreterNode node, String exprNodeStr) {
		super(node);
		Objects.requireNonNull(exprNodeStr);
		this.exprNodeStr = exprNodeStr;
	}

	@Override
	public String toString() {
		return "\""+exprNodeStr+"\"";
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return List.of();
	}

	@Override
	public int hashCode() {
		return Objects.hash(exprNodeStr);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpressionNodeSemantic other = (ExpressionNodeSemantic) obj;
		return Objects.equals(exprNodeStr, other.exprNodeStr);
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		return this;
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitExpressionNodeSemantic(this);
	}
}
