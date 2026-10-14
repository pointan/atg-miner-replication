package at.fhooe.se.atgminer.interpreterextensions;

import at.fhooe.se.codeanalysis.adele.interpreter.runtime.ListObject;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.RuntimeObject;

import java.util.List;
import java.util.stream.IntStream;

public class ListObjectComparer implements RuntimeObjectComparer<ListObject, ListObject> {

    private final RuntimeObjectComparer<RuntimeObject, RuntimeObject> baseComparer;

    public ListObjectComparer(RuntimeObjectComparer<RuntimeObject, RuntimeObject> baseComparer) {
        this.baseComparer = baseComparer;
    }

    @Override
    public boolean compareObjects(ListObject obj, ListObject comp) {
        RuntimeObject[] elements = obj.elements();
        RuntimeObject[] compElements = comp.elements();
        if (elements.length != compElements.length) {
            System.out.println("List differ in length");
            return false;
        }

        return IntStream.range(0, elements.length).allMatch(i -> baseComparer.compareObjects(elements[i], compElements[i]));
    }
}
