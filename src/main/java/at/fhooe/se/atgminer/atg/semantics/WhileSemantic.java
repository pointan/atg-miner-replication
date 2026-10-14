package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Semantic to represent a while loop</p>
 */
public class WhileSemantic extends ConditionalSemantic {

    private final BaseSemantic body;

    public WhileSemantic(BaseSemantic condition, BaseSemantic body) {
        this(null, condition, body);
    }

    public WhileSemantic(InterpreterNode node, BaseSemantic condition, BaseSemantic body) {
        super(node, condition);
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WhileSemantic that = (WhileSemantic) o;
        return Objects.equals(body, that.body) && Objects.equals(getCondition(), that.getCondition());
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return List.of(body, getCondition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(body, getCondition());
    }

    @Override
    public String toString() {
        return String.format("while(%s) { %s }", getCondition(), body);
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new WhileSemantic(getBasedOn(), getCondition().replace(replacement), body.replace(replacement));
    }

    public BaseSemantic getBody() {
        return body;
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitWhileSemantic(this);
    }
}
