package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;

/**
 * <p>Representation of a null or none semantic</p>
 */
public class NullSemantic extends BaseSemantic {

    public NullSemantic(InterpreterNode node) {
        super(node);
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return List.of();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o.getClass().equals(this.getClass());
    }

    @Override
    public int hashCode() {
        return 25933195;
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        return this;
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitNullSemantic(this);
    }
}
