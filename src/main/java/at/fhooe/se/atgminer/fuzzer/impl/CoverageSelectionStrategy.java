package at.fhooe.se.atgminer.fuzzer.impl;

import at.fhooe.se.atgminer.fuzzer.AlternativeSelectionStrategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Tries to at least select every possible element once.</p>
 */
public class CoverageSelectionStrategy<T, X> implements AlternativeSelectionStrategy<T> {

    private final Set<X> selectedOptions;
    private final AlternativeSelectionStrategy<T> baseStrategy;
    private final Function<T, X> keyExtractor;

    public CoverageSelectionStrategy(AlternativeSelectionStrategy<T> baseStrategy, Function<T, X> keyExtractor) {
        this.baseStrategy = baseStrategy;
        this.keyExtractor = keyExtractor;
        selectedOptions = new HashSet<>();
    }

    @Override
    public T selectAlternative(List<T> elements) {
        List<T> collect = elements.stream().filter(x -> !selectedOptions.contains(keyExtractor.apply(x))).collect(Collectors.toList());
        if (collect.isEmpty()) collect = elements;
        T selected = baseStrategy.selectAlternative(collect);
        selectedOptions.add(keyExtractor.apply(selected));
        return selected;
    }
}
