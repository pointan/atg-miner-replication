package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Semantic that represents a dictionary</p>
 */
public class DictSemantic extends BaseSemantic {
    public static class Entry extends BaseSemantic {
        private final BaseSemantic key;
        private final BaseSemantic value;

        public Entry(InterpreterNode node, final BaseSemantic key, final BaseSemantic value) {
            super(node);
            this.key = Objects.requireNonNull(key);
            this.value = Objects.requireNonNull(value);
        }

        public BaseSemantic getKey() {
            return key;
        }

        public BaseSemantic getValue() {
            return value;
        }

        @Override
        public List<BaseSemantic> getChildren() {
            return List.of(key, value);
        }

        @Override
        public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
            if (replacement.containsKey(this)) {
                return replacement.get(this);
            }
            return new Entry(getBasedOn(), key.replace(replacement), value.replace(replacement));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry that = (Entry) o;
            return Objects.equals(getKey(), that.getKey()) && Objects.equals(getValue(), that.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getKey(), getValue());
        }

        @Override
        public String toString() {
            return getKey() + ":" + getValue();
        }
    }

    private final List<Entry> entries;

    public DictSemantic(InterpreterNode node, Entry... entries) {
        super(node);
        this.entries = Arrays.asList(entries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictSemantic that = (DictSemantic) o;
        return Objects.equals(entries, that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entries);
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new DictSemantic(
                getBasedOn(),
                entries.stream().map(x -> (Entry) x.replace(replacement)).toArray(Entry[]::new)
        );
    }

    @Override
    public String toString() {
        return "{" + entries.stream().map(Objects::toString).collect(Collectors.joining(",")) + "}";
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitDictSemantic(this);
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
