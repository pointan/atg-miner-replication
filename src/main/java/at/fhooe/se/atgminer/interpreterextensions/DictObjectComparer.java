package at.fhooe.se.atgminer.interpreterextensions;

import at.fhooe.se.codeanalysis.adele.interpreter.runtime.DictObject;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.RuntimeObject;

/**
 * <p>Compares two dictionaries like Python does: same keys (independent of insertion order) and equal values.</p>
 */
public class DictObjectComparer implements RuntimeObjectComparer<DictObject, DictObject> {

    private final RuntimeObjectComparer<RuntimeObject, RuntimeObject> baseComparer;

    public DictObjectComparer(RuntimeObjectComparer<RuntimeObject, RuntimeObject> baseComparer) {
        this.baseComparer = baseComparer;
    }

    @Override
    public boolean compareObjects(DictObject obj, DictObject comp) {
        if (obj.size() != comp.size()) {
            System.out.println("Dicts differ in size");
            return false;
        }

        return obj.getItems().stream().allMatch(item -> {
            if (!comp.containsKey(item.first)) {
                System.out.println("Key \"" + item.first + "\" missing in dict " + comp);
                return false;
            }
            return baseComparer.compareObjects(item.second, comp.get(item.first));
        });
    }
}
