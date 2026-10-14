package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Map;

/**
 * <p>Semantic to represent a greater operation</p>
 */
public class GreaterSemantic extends BinaryExprSemantic {

    public GreaterSemantic(InterpreterNode node, BaseSemantic leftOperand, BaseSemantic rightOperand) {
        super(node, leftOperand, rightOperand);
    }

    @Override
    public String toString() {
        return getLeftOperand() + "&gt;" + getRightOperand();
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new GreaterSemantic(getBasedOn(), getLeftOperand().replace(replacement), getRightOperand().replace(replacement));
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitGreaterSemantic(this);
    }
}
