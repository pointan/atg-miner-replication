package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.atgminer.util.ListUtil;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Semantic Object to contain method invokes.</p>
 */
public class MethodInvokeSemantic extends BaseSemantic {
    private final BaseSemantic obj;
    private final String funcName;
    private final BaseSemantic[] actualParams;

    public MethodInvokeSemantic(InterpreterNode node, BaseSemantic obj, String funcName, BaseSemantic[] actualParams) {
        super(node);
        this.obj = obj;
        this.funcName = funcName;
        this.actualParams = actualParams;
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return ListUtil.concat(List.of(obj), Arrays.asList(actualParams));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodInvokeSemantic that = (MethodInvokeSemantic) o;
        return Objects.equals(obj, that.obj) && Objects.equals(funcName, that.funcName) && Objects.deepEquals(actualParams, that.actualParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(obj, funcName, Arrays.hashCode(actualParams));
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new MethodInvokeSemantic(getBasedOn(), obj.replace(replacement), funcName, Stream.of(actualParams).map(x -> x.replace(replacement)).toArray(BaseSemantic[]::new));
    }

    @Override
    public String toString() {
        return obj + "." + funcName + "(" + Stream.of(actualParams).map(Objects::toString).collect(Collectors.joining(", ")) + ")";
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitMethodInvokeSemantic(this);
    }

    public BaseSemantic getObj() {
        return obj;
    }

    public String getFuncName() {
        return funcName;
    }

    public BaseSemantic[] getActualParams() {
        return actualParams;
    }
}
