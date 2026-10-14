package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Semantic to contain a function invoke call.</p>
 */
public class FuncInvokeSemantic extends BaseSemantic {

    private final String funcName;
    private final BaseSemantic[] actualParams;

    public FuncInvokeSemantic(InterpreterNode basedOn, String funcName, BaseSemantic[] actualParams) {
        super(basedOn);
        this.funcName = funcName;
        this.actualParams = actualParams;
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return List.of(actualParams);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FuncInvokeSemantic that = (FuncInvokeSemantic) o;
        return Objects.equals(funcName, that.funcName) && Objects.deepEquals(actualParams, that.actualParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(funcName, Arrays.hashCode(actualParams));
    }

    @Override
    public String toString() {
        return funcName + "(" + Stream.of(actualParams).map(Objects::toString).collect(Collectors.joining(", ")) + ")";
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new FuncInvokeSemantic(getBasedOn(), funcName, Stream.of(actualParams).map(x -> x.replace(replacement)).toArray(BaseSemantic[]::new));
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitFuncInvokeSemantic(this);
    }

    public String getFuncName() {
        return funcName;
    }

    public BaseSemantic[] getActualParams() {
        return actualParams;
    }
}
