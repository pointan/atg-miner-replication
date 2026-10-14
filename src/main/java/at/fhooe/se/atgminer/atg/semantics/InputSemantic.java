package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;

/**
 * <p>Semantic representing a user prompting an input command</p>
 */
public class InputSemantic extends FunctionSemantic {
    public InputSemantic(InterpreterNode node) {
        super(node);
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return List.of();
    }

    @Override
    public int hashCode() {
        return 326822272;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return this.getClass() == obj.getClass();
    }

    @Override
    public String toString() {
        return "input()";
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        } else {
            return new InputSemantic(getBasedOn());
        }
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitInputSemantic(this);
    }
}
