package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Semantic Node to contain Lists</p>
 */
public class ListSemantic extends BaseSemantic {

    private final BaseSemantic[] elements;

    public ListSemantic(InterpreterNode node, BaseSemantic... elements) {
        super(node);
        this.elements = Objects.requireNonNull(elements);
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return List.of(elements);
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new ListSemantic(getBasedOn(), Stream.of(elements).map(x -> x.replace(replacement)).toArray(BaseSemantic[]::new));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListSemantic that = (ListSemantic) o;
        return Objects.deepEquals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }

    @Override
    public String toString() {
        return "[" + Arrays.stream(elements).map(Object::toString).collect(Collectors.joining(",")) +"]";
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitListSemantic(this);
    }
}
