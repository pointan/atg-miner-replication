package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Map;

/**
 * <p>Semantic to express an Bitwise or: |</p>
 */
public class BitOrSemantic extends BinaryExprSemantic {
    public BitOrSemantic(InterpreterNode node, BaseSemantic leftOperand, BaseSemantic rightOperand) {
        super(node, leftOperand, rightOperand);
    }

    @Override
    public String toString() {
        return getLeftOperand() + "|" + getRightOperand();
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new BitOrSemantic(getBasedOn(), getLeftOperand().replace(replacement), getRightOperand().replace(replacement));
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitBitOrSemantic(this);
    }
}
