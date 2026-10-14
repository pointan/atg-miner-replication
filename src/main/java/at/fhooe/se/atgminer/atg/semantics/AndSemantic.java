package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Map;

/**
 * <p>Semantic to express an AND node</p>
 */
public class AndSemantic extends BinaryExprSemantic {
	public AndSemantic(InterpreterNode node, BaseSemantic leftOperand, BaseSemantic rightOperand) {
		super(node, leftOperand, rightOperand);
	}

	@Override
	public String toString() {
		return "" + getLeftOperand() + " AND " + getRightOperand();
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new AndSemantic(getBasedOn(), getLeftOperand().replace(replacement), getRightOperand().replace(replacement));
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitAndSemantic(this);
	}
}
