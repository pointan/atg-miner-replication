package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;

/**
 * <p>Class that represents a semantic error</p>
 */
public class ErrorSemantic extends BaseSemantic {
    public ErrorSemantic(InterpreterNode node) {
        super(node);
    }

    public String toString() {
        return "throw SemanticError";
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return List.of();
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o.getClass().equals(this.getClass());
    }

    @Override
    public int hashCode() {
        return 489465489;
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitErrorSemantic(this);
    }
}
