package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Represents the call to the int function</p>
 */
public class IntSemantic extends FunctionSemantic {

    private final BaseSemantic parameter;
    private final BaseSemantic base;

    public IntSemantic(InterpreterNode node, BaseSemantic parameter, BaseSemantic base) {
        super(node);
        this.parameter = parameter;
        this.base = base;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IntSemantic that = (IntSemantic) o;
        return Objects.equals(parameter, that.parameter) && Objects.equals(base, that.base);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameter, base);
    }

    @Override
    public String toString() {
        return base == null ? "int(" + parameter + ")" : "int(" + parameter + ", " + base + ")";
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new IntSemantic(getBasedOn(), parameter.replace(replacement), base != null ? base.replace(replacement) : null);
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return List.of(parameter, base);
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitIntSemantic(this);
    }

    public BaseSemantic getParameter() {
        return parameter;
    }

    public BaseSemantic getBase() {
        return base;
    }
}