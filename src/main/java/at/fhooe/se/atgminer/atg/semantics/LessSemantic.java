package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Map;

/**
 * <p>Semantic to represent a less operation</p>
 */
public class LessSemantic extends BinaryExprSemantic {

    public LessSemantic(InterpreterNode node, BaseSemantic leftOperand, BaseSemantic rightOperand) {
        super(node, leftOperand, rightOperand);
    }

    @Override
    public String toString() {
        return getLeftOperand() + "&lt;" + getRightOperand();
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new LessSemantic(getBasedOn(), getLeftOperand().replace(replacement), getRightOperand().replace(replacement));
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitLessSemantic(this);
    }
}
