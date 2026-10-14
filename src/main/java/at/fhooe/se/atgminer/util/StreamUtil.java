package at.fhooe.se.atgminer.util;

import java.util.Collection;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Utils class to help with various stream operations</p>
 */
public class StreamUtil {
    private static final Consumer<?> EMPTY_CONSUMER = x -> {
    };

    /**
     * Creates a consumer that does nothing.
     *
     * @param <T> the generic type of the input of the consumer
     * @return an empty consumer
     */
    public static <T> Consumer<T> emptyConsumer() {
        return CastUtil.cast(EMPTY_CONSUMER);
    }

    /**
     * creates a binary operator merges two elements, if both elements are the same, but just returning the first one
     * otherwise throws an {@link IllegalStateException}.
     *
     * @param <T> the type of the objects that are compared
     * @return a binary operator that merges two elements
     */
    public static <T> BinaryOperator<T> mergeIfEqualOperator() {
        return (t1, t2) -> {
            if (t1 == t2) {
                return t1;
            }
            throw new IllegalStateException("Two different elements with same id");
        };
    }

    /**
     * Create a binary operator, that merges two lists
     *
     * @param <T> the type of the elements in the list
     * @return a binary operator that merges two lists
     */
    public static <T> BinaryOperator<List<T>> listMergeOperator() {
        return (x, y) -> Stream.of(x, y).flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * Combines multiple predicates with an or operation
     *
     * @param first      the first predicate
     * @param predicates the list of following predicates
     * @param <T>        the type of the predicates
     * @return the composed predicate connected with or
     */
    @SafeVarargs
    public static <T> Predicate<T> or(Predicate<T> first, Predicate<T>... predicates) {
        for (Predicate<T> predicate : predicates) {
            first = first.or(predicate);
        }
        return first;
    }
}
