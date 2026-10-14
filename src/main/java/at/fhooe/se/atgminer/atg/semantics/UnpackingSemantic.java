package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Represents an unpacking operator</p>
 */
public class UnpackingSemantic extends FunctionSemantic {
    private final BaseSemantic parameter;

    public UnpackingSemantic(InterpreterNode node, BaseSemantic parameter) {
        super(node);
        this.parameter = parameter;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UnpackingSemantic that = (UnpackingSemantic) o;
        return Objects.equals(parameter, that.parameter);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parameter);
    }

    @Override
    public String toString() {
        return "*" + parameter;
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return List.of(parameter);
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new UnpackingSemantic(getBasedOn(), parameter.replace(replacement));
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitUnpackingSemantic(this);
    }

    public BaseSemantic getParameter() {
        return parameter;
    }
}
