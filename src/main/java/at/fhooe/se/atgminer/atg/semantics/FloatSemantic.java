package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Function semantic that transforms a string into a float</p>
 */
public class FloatSemantic extends FunctionSemantic {
    private final BaseSemantic parameter;

    public FloatSemantic(InterpreterNode node, BaseSemantic parameter) {
        super(node);
        this.parameter = parameter;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FloatSemantic that = (FloatSemantic) o;
        return Objects.equals(parameter, that.parameter);
    }

    @Override
    public String toString() {
        return "float(" + parameter + ")";
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new FloatSemantic(getBasedOn(), parameter.replace(replacement));
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return List.of(parameter);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parameter);
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitFloatSemantic(this);
    }

    public BaseSemantic getParameter() {
        return parameter;
    }
}
