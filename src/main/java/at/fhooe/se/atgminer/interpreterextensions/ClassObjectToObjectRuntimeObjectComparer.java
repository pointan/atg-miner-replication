package at.fhooe.se.atgminer.interpreterextensions;

import at.fhooe.se.atgminer.atg.LL1GrammarInterpreter;
import at.fhooe.se.atgminer.atg.semantics.VariableSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.ClassObject;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.RuntimeObject;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ClassObjectToObjectRuntimeObjectComparer implements RuntimeObjectComparer<ClassObject, LL1GrammarInterpreter.ObjectRuntimeObject> {

    private final RuntimeObjectComparer<RuntimeObject, RuntimeObject> baseComparer;

    public ClassObjectToObjectRuntimeObjectComparer(RuntimeObjectComparer<RuntimeObject, RuntimeObject> baseComparer) {
        this.baseComparer = baseComparer;
    }

    @Override
    public boolean compareObjects(ClassObject obj, LL1GrammarInterpreter.ObjectRuntimeObject comp) {
        if (!obj.getType().getTypeName().equals(comp.getClassName())) {
            System.out.println("missmatch in classname");
            return false;
        }
        return Stream.of(obj.getMemberNames()).allMatch(x -> {
            RuntimeObject member = obj.getMember(x);
            RuntimeObject memberComp = comp.getObjectFrame().getVariable(new VariableSemantic(null, x));
            return baseComparer.compareObjects(member, memberComp);
        });
    }
}
