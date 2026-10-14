package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.atgminer.util.CastUtil;
import at.fhooe.se.atgminer.util.ListUtil;
import at.fhooe.se.atgminer.util.StringUtil;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MethodDefSemantic extends DefSemantic {

    private final String className;
    private final String methodName;
    private final List<BaseSemantic> statements;
    private final List<FormalParamDefSemantic > formalParamDefs;

    public MethodDefSemantic(InterpreterNode basedOn, String className, String methodName, List<BaseSemantic> statements, List<FormalParamDefSemantic> formalParamDefs) {
        super(basedOn);
        this.className = className;
        this.methodName = methodName;
        this.statements = statements;
        this.formalParamDefs = formalParamDefs;
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return ListUtil.concat(statements, CastUtil.cast(formalParamDefs));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MethodDefSemantic that = (MethodDefSemantic) o;
        return Objects.equals(className, that.className) && Objects.equals(methodName, that.methodName) && Objects.equals(statements, that.statements) && Objects.equals(formalParamDefs, that.formalParamDefs);
    }

    @Override
    public String toString() {
        return "function " + getClassName() + "." + getMethodName() + "(" + getFormalParamDefs().stream().map(Object::toString).collect(Collectors.joining(", ")) + ") {\n"
                + getStatements().stream().map(Object::toString).map(x -> "  " + x).collect(Collectors.joining("\n")) +
                "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, methodName, statements, formalParamDefs);
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        throw new UnsupportedOperationException();
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<BaseSemantic> getStatements() {
        return statements;
    }

    public List<FormalParamDefSemantic> getFormalParamDefs() {
        return formalParamDefs;
    }
}
