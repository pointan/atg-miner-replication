package at.fhooe.se.atgminer.interpreterextensions;

import at.fhooe.se.atgminer.atg.LL1GrammarInterpreter;
import at.fhooe.se.atgminer.util.CastUtil;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.ClassObject;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.DictObject;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.ListObject;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.RuntimeObject;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.ValueObject;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public interface RuntimeObjectComparer<T1 extends RuntimeObject, T2 extends RuntimeObject> {

    boolean compareObjects(T1 obj, T2 comp);

    static <T1 extends RuntimeObject, T2 extends RuntimeObject> boolean compareRuntimeObjects(T1 obj, T2 comp) {
        return BaseRuntimeObjectComparer.getDefaultComparer().compareObjects(obj, comp);
    }

    class BaseRuntimeObjectComparer implements RuntimeObjectComparer<RuntimeObject, RuntimeObject> {

        private static BaseRuntimeObjectComparer DEFAULT_INSTANZ;

        public static RuntimeObjectComparer<RuntimeObject, RuntimeObject> getDefaultComparer() {
            if (DEFAULT_INSTANZ == null) {
                DEFAULT_INSTANZ = new BaseRuntimeObjectComparer();
                DEFAULT_INSTANZ.registerNew(ClassObject.class, LL1GrammarInterpreter.ObjectRuntimeObject.class, new ClassObjectToObjectRuntimeObjectComparer(DEFAULT_INSTANZ));
                DEFAULT_INSTANZ.registerNew(ListObject.class, ListObject.class, new ListObjectComparer(DEFAULT_INSTANZ));
                DEFAULT_INSTANZ.registerNew(ValueObject.class, ValueObject.class, new ValueObjectComparer());
                DEFAULT_INSTANZ.registerNew(DictObject.class, DictObject.class, new DictObjectComparer(DEFAULT_INSTANZ));
            }
            return DEFAULT_INSTANZ;
        }

        @Override
        public boolean compareObjects(RuntimeObject obj, RuntimeObject comp) {
            Class<? extends RuntimeObject> t1 = obj.getClass();
            Class<? extends RuntimeObject> t2 = comp.getClass();

            var comparer = lookup(t1, t2);
            if (comparer.isPresent()) {
                return comparer.get().compareObjects(CastUtil.cast(obj), CastUtil.cast(comp));
            }

            var comparerRetry = lookup(t2, t1);
            if (comparerRetry.isPresent()) {
                return comparerRetry.get().compareObjects(CastUtil.cast(comp), CastUtil.cast(obj));
            }

            // no specific comparer registered for this type combination (e.g. None vs. value): compare the textual
            // representation, as done by the original evaluation
            boolean equal = obj.toString().equals(comp.toString());
            if (!equal) {
                System.out.println("\"" + obj + "\" (" + t1.getSimpleName() + ") does not match with \"" + comp + "\" (" + t2.getSimpleName() + ")");
            }
            return equal;
        }

        private static class RegistryEntry<T1 extends RuntimeObject, T2 extends RuntimeObject> {
            private final Class<T1> t1;
            private final Class<T2> t2;
            private final RuntimeObjectComparer<T1, T2> comparer;

            public RegistryEntry(Class<T1> t1, Class<T2> t2, RuntimeObjectComparer<T1, T2> comparer) {
                this.t1 = t1;
                this.t2 = t2;
                this.comparer = comparer;
            }
        }

        private final Set<RegistryEntry<?, ?>> registry = new HashSet<>();

        public <T1 extends RuntimeObject, T2 extends RuntimeObject> void registerNew(Class<T1> type1, Class<T2> type2, RuntimeObjectComparer<T1, T2> comparer) {
            registry.add(new RegistryEntry<>(type1, type2, comparer));
        }

        private <T1 extends RuntimeObject, T2 extends RuntimeObject> Optional<RuntimeObjectComparer<T1, T2>> lookup(Class<T1> t1, Class<T2> t2) {
            return registry.stream().filter(x -> x.t1.equals(t1) && x.t2.equals(t2)).map(x -> x.comparer).map(CastUtil::<RuntimeObjectComparer<T1, T2>>cast).findFirst();
        }
    }

}
