package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.atgminer.util.ListUtil;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>Semantic node for array access</p>
 */
public class ArrayAccessSemantic extends BaseSemantic {
    private final BaseSemantic arrayName;
    private final BaseSemantic[] subscripts;

    public ArrayAccessSemantic(InterpreterNode node, BaseSemantic arrayName, BaseSemantic[] subscripts) {
        super(node);
        this.arrayName = arrayName;
        this.subscripts = subscripts;
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return ListUtil.concat(List.of(arrayName), List.of(subscripts));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayAccessSemantic that = (ArrayAccessSemantic) o;
        return Objects.equals(arrayName, that.arrayName) && Objects.deepEquals(subscripts, that.subscripts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arrayName, Arrays.hashCode(subscripts));
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new ArrayAccessSemantic(
                getBasedOn(),
                arrayName.replace(replacement),
                Arrays.stream(subscripts).map(x -> x.replace(replacement)).toArray(BaseSemantic[]::new)
        );
    }

    @Override
    public String toString() {
        return arrayName.toString() + "[" + Arrays.stream(subscripts).map(Objects::toString).collect(Collectors.joining(",")) + "]";
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitArrayAccessSemantic(this);
    }

    public BaseSemantic getArrayName() {
        return arrayName;
    }

    public BaseSemantic[] getSubscripts() {
        return subscripts;
    }
}
