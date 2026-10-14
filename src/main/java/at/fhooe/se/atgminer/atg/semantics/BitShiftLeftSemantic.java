package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Map;

/**
 * <p>Semantic representation of a BitShiftLeft: << </p>
 */
public class BitShiftLeftSemantic extends BinaryExprSemantic {
    public BitShiftLeftSemantic(InterpreterNode node, BaseSemantic leftOperand, BaseSemantic rightOperand) {
        super(node, leftOperand, rightOperand);
    }

    @Override
    public String toString() {
        return getLeftOperand() + " &lt;&lt; " + getRightOperand();
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new BitShiftLeftSemantic(getBasedOn(), getLeftOperand().replace(replacement), getRightOperand().replace(replacement));
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitBitShiftLeftSemantic(this);
    }
}