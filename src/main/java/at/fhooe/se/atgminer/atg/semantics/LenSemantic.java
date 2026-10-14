package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Returns the len of a list of a string</p>
 */
public class LenSemantic extends FunctionSemantic {
    private final BaseSemantic parameter;

    public LenSemantic(InterpreterNode node, BaseSemantic parameter) {
        super(node);
        this.parameter = parameter;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LenSemantic that = (LenSemantic) o;
        return Objects.equals(parameter, that.parameter);
    }

    @Override
    public String toString() {
        return "len(" + parameter + ")";
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new LenSemantic(getBasedOn(), parameter.replace(replacement));
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return List.of(parameter);
    }

    public BaseSemantic getParameter() {
        return parameter;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parameter);
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitLenSemantic(this);
    }
}
