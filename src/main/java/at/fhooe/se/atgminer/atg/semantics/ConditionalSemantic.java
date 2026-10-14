package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Abstract base class for code that is only executed in certain conditions. (e.g. if, while, ...)</p>
 */
public abstract class ConditionalSemantic extends BaseSemantic {
    private final BaseSemantic condition;

    public ConditionalSemantic(InterpreterNode node, BaseSemantic condition) {
        super(node);
        this.condition = condition;
    }

    public BaseSemantic getCondition() {
        return condition;
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitConditionalSemantic(this);
    }
}
