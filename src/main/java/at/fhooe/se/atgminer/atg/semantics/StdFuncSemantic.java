package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>This combines all other StdFuncSemantics that do not have an own class</p>
 */
public class StdFuncSemantic extends FunctionSemantic {
    private final String functionName;
    private final BaseSemantic[] parameter;

    public StdFuncSemantic(InterpreterNode node, String functionName, BaseSemantic[] parameter) {
        super(node);
        this.functionName = functionName;
        this.parameter = parameter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StdFuncSemantic that = (StdFuncSemantic) o;
        return Objects.equals(functionName, that.functionName) && Objects.deepEquals(parameter, that.parameter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionName, Arrays.hashCode(parameter));
    }

    @Override
    public String toString() {
        return functionName + "(" + Arrays.stream(parameter).map(Objects::toString).collect(Collectors.joining(", ")) + ")";
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return Arrays.asList(parameter);
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new StdFuncSemantic(getBasedOn(), functionName, Arrays.stream(parameter).map(x -> x.replace(replacement)).toArray(BaseSemantic[]::new));
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitStdFuncSemantic(this);
    }

    public String getFunctionName() {
        return functionName;
    }

    public BaseSemantic[] getParameter() {
        return parameter;
    }
}
