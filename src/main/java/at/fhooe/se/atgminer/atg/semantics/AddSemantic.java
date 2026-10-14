package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Map;

/**
 * <p>Semantic class, that represents an add operation</p>
 */
public class AddSemantic extends BinaryExprSemantic {

	public AddSemantic(InterpreterNode node, BaseSemantic leftOperand, BaseSemantic rightOperand) {
		super(node, leftOperand, rightOperand);
	}

	@Override
	public String toString() {
		return "(" + getLeftOperand() + " + " + getRightOperand() + ")";
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new AddSemantic(getBasedOn(), getLeftOperand().replace(replacement), getRightOperand().replace(replacement));
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitAddSemantic(this);
	}
}
