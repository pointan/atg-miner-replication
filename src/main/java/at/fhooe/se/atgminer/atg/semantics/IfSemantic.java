package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Semantic that represents an if node</p>
 */
public class IfSemantic extends ConditionalSemantic {

    private final BaseSemantic thenBlock;
    private final BaseSemantic elseBlock;

    public IfSemantic(BaseSemantic condition, BaseSemantic thenBlock, BaseSemantic elseBlock) {
        this(null, condition, thenBlock, elseBlock);
    }

    public IfSemantic(InterpreterNode node, BaseSemantic condition, BaseSemantic thenBlock, BaseSemantic elseBlock) {
        super(node, condition);
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IfSemantic that = (IfSemantic) o;
        return Objects.equals(thenBlock, that.thenBlock) && Objects.equals(elseBlock, that.elseBlock) && Objects.equals(getCondition(), that.getCondition());
    }

    @Override
    public List<BaseSemantic> getChildren() {
        if (thenBlock == null)
            return List.of(elseBlock, getCondition());
        if (elseBlock == null)
            return List.of(thenBlock, getCondition());
        return List.of(thenBlock, elseBlock, getCondition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(thenBlock, elseBlock, getCondition());
    }

    @Override
    public String toString() {
        return String.format("if(%s) { %s } else { %s }", getCondition(), thenBlock, elseBlock);
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new IfSemantic(getBasedOn(), getCondition().replace(replacement), thenBlock == null ? null : thenBlock.replace(replacement), elseBlock == null ? null : elseBlock.replace(replacement));
    }

    public BaseSemantic getThenBlock() {
        return thenBlock;
    }

    public BaseSemantic getElseBlock() {
        return elseBlock;
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitIfSemantic(this);
    }
}
