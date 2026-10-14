package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FormalParamDefSemantic extends BaseSemantic {

    private final String ident;

    public FormalParamDefSemantic(InterpreterNode basedOn, String ident) {
        super(basedOn);
        this.ident = ident;
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return List.of();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FormalParamDefSemantic that = (FormalParamDefSemantic) o;
        return Objects.equals(ident, that.ident);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ident);
    }

    @Override
    public String toString() {
        return ident;
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        return new FormalParamDefSemantic(getBasedOn(), getIdent());
    }

    public String getIdent() {
        return ident;
    }
}
