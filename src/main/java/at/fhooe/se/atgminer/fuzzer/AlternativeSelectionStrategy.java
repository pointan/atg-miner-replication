package at.fhooe.se.atgminer.fuzzer;

import java.util.List;

/**
 * <p>Strategy on how an alternative is selected.</p>
 */
public interface AlternativeSelectionStrategy<T> {
    /**
     * Selects an element from the given list of elements, based on a given strategy
     *
     * @param elements the elements
     * @return the selected element
     */
    T selectAlternative(List<T> elements);
}
