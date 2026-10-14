package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Map;

/**
 * <p>Semantic class to represent an Unary Minus Operation</p>
 */
public class UnaryMinusSemantic extends UnaryExpressionSemantic {

	public UnaryMinusSemantic(InterpreterNode node, BaseSemantic operand) {
		super(node, operand);
	}

	@Override
	public String toString() {
		return "-" + getOperand();
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new UnaryMinusSemantic(getBasedOn(), getOperand().replace(replacement));
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitUnaryMinusSemantic(this);
	}
}
