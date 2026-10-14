package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.atgminer.util.CastUtil;
import at.fhooe.se.atgminer.util.ListUtil;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FuncDefSemantic extends DefSemantic {

    private final String funcName;
    private final List<BaseSemantic> statements;
    private final List<FormalParamDefSemantic> paramDefSemantics;

    public FuncDefSemantic(InterpreterNode basedOn, String funcName, List<BaseSemantic> statements, List<FormalParamDefSemantic> paramDefSemantics) {
        super(basedOn);
        this.funcName = funcName;
        this.statements = statements;
        this.paramDefSemantics = paramDefSemantics;
    }


    @Override
    public List<BaseSemantic> getChildren() {
        return ListUtil.concat(statements, CastUtil.cast(paramDefSemantics));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FuncDefSemantic that = (FuncDefSemantic) o;
        return Objects.equals(funcName, that.funcName) && Objects.equals(statements, that.statements) && Objects.equals(paramDefSemantics, that.paramDefSemantics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(funcName, statements, paramDefSemantics);
    }

    @Override
    public String toString() {
        return "function " + getFuncName() + "(" + getParamDefSemantics().stream().map(Object::toString).collect(Collectors.joining(", ")) + ") {\n"
                + getStatements().stream().map(Object::toString).map(x -> "  " + x).collect(Collectors.joining("\n")) +
                "}";
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getFuncName() {
        return funcName;
    }

    public List<BaseSemantic> getStatements() {
        return statements;
    }

    public List<FormalParamDefSemantic> getParamDefSemantics() {
        return paramDefSemantics;
    }
}
