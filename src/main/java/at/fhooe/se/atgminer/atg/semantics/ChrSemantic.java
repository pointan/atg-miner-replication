package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Class that represents the Chr function.</p>
 */
public class ChrSemantic extends FunctionSemantic {

    private final BaseSemantic parameter;

    public ChrSemantic(InterpreterNode node, BaseSemantic parameter) {
        super(node);
        this.parameter = parameter;
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
        return new ChrSemantic(getBasedOn(), parameter.replace(replacement));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChrSemantic that = (ChrSemantic) o;
        return Objects.equals(parameter, that.parameter);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parameter);
    }

    @Override
    public String toString() {
        return "Chr(" + parameter + ")";
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitChrSemantic(this);
    }

    public BaseSemantic getParameter() {
        return parameter;
    }
}
