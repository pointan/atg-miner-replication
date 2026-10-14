package at.fhooe.se.atgminer.interpreterextensions;

import at.fhooe.se.codeanalysis.adele.interpreter.runtime.ValueObject;

public class ValueObjectComparer implements RuntimeObjectComparer<ValueObject, ValueObject> {
    @Override
    public boolean compareObjects(ValueObject obj, ValueObject comp) {
        if (!obj.getValue().equals(comp.getValue())) {
            System.out.println("\"" + obj.getValue() + "\" does not match with \"" + comp.getValue() + "\"");
            return false;
        }
        return true;
    }
}
