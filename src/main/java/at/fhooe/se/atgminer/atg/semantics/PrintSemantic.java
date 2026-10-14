package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Semantic that represents a print statement</p>
 */
public class PrintSemantic extends FunctionSemantic {
    private final BaseSemantic parameter;

    public PrintSemantic(InterpreterNode node, BaseSemantic parameter) {
        super(node);
        this.parameter = parameter;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PrintSemantic that = (PrintSemantic) o;
        return Objects.equals(parameter, that.parameter);
    }

    @Override
    public String toString() {
        return "print(" + parameter + ")";
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new PrintSemantic(getBasedOn(), parameter.replace(replacement));
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
        return visitor.visitPrintSemantic(this);
    }

    public BaseSemantic getParameter() {
        return parameter;
    }
}
