package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Representation of the Slice operator</p>
 */
public class SliceSemantic extends BaseSemantic {
    private final BaseSemantic sequenceName;
    private final BaseSemantic beginIdx;
    private final BaseSemantic endIdx;

    public SliceSemantic(InterpreterNode node, BaseSemantic sequenceName, BaseSemantic beginIdx, BaseSemantic endIdx) {
        super(node);
        this.sequenceName = sequenceName;
        this.beginIdx = beginIdx;
        this.endIdx = endIdx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SliceSemantic that = (SliceSemantic) o;
        return Objects.equals(sequenceName, that.sequenceName) && Objects.equals(beginIdx, that.beginIdx) && Objects.equals(endIdx, that.endIdx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequenceName, beginIdx, endIdx);
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return List.of(sequenceName, beginIdx, endIdx);
    }

    @Override
    public String toString() {
        return sequenceName.toString() + "[" + beginIdx.toString() + ":" + endIdx.toString() + "]";
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new SliceSemantic(getBasedOn(), sequenceName.replace(replacement), beginIdx.replace(replacement), endIdx.replace(replacement));
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitSliceSemantic(this);
    }

    public BaseSemantic getSequenceName() {
        return sequenceName;
    }

    public BaseSemantic getBeginIdx() {
        return beginIdx;
    }

    public BaseSemantic getEndIdx() {
        return endIdx;
    }
}
