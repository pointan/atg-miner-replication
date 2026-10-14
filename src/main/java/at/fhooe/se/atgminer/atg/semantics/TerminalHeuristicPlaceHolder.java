package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Just a placeholder to know that we have used a heuristic here to place this terminal symbol.</p>
 */
public class TerminalHeuristicPlaceHolder extends BaseSemantic {

    private final BaseSemantic node;

    public TerminalHeuristicPlaceHolder(InterpreterNode basedOn, BaseSemantic node) {
        super(basedOn);
        this.node = node;
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return List.of(node);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TerminalHeuristicPlaceHolder that = (TerminalHeuristicPlaceHolder) o;
        return Objects.equals(node, that.node);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(node);
    }

    @Override
    public String toString() {
        return node.toString();
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new TerminalHeuristicPlaceHolder(getBasedOn(), node.replace(replacement));
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitTerminalHeuristicPlaceHolder(this);
    }

    public BaseSemantic getNode() {
        return node;
    }
}
