package at.fhooe.se.atgminer.fuzzer.impl;

import at.fhooe.se.atgminer.fuzzer.AlternativeSelectionStrategy;
import at.fhooe.se.atgminer.util.Random;

import java.util.List;

/**
 * <p>Selects a random alternative based</p>
 */
public class RandomAlternativeSelectionStrategy<T> implements AlternativeSelectionStrategy<T> {
    private final Random random;

    public RandomAlternativeSelectionStrategy(Random random) {
        this.random = random;
    }

    @Override
    public T selectAlternative(List<T> elements) {
        if (elements.isEmpty()) throw new IllegalStateException("Does not support empty list");
        if (elements.size() == 1) return elements.get(0);
        return elements.get(random.nextInt(elements.size()));
    }
}
