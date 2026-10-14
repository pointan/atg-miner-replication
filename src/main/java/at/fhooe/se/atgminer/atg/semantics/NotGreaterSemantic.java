package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Map;

/**
 * Objects of this class represent the not greater operator.
 */
public class NotGreaterSemantic extends BinaryExprSemantic {
	public NotGreaterSemantic(InterpreterNode node, BaseSemantic leftOperand, BaseSemantic rightOperand) {
		super(node, leftOperand, rightOperand);
	}

	@Override
	public String toString() {
		return "" + getLeftOperand() + " <= " + getRightOperand();
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new NotGreaterSemantic(getBasedOn(), getLeftOperand().replace(replacement), getRightOperand().replace(replacement));
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitNotGreaterSemantic(this);
	}

}