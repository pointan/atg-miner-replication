package at.fhooe.se.atgminer.fuzzer.impl;

import at.fhooe.se.atgminer.fuzzer.AlternativeSelectionStrategy;
import at.fhooe.se.atgminer.util.Random;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * <p>Implementation of the cooldown algorithm 4.1 of the master thesis Generative Grammar-based Blackbox Fuzzing of a
 * Custom-made Data Store. The cooldown strategy is based on the findings of the development of the Grammarinator
 * (<a href="https://doi.org/10.1145/3278186.3278193">DOI</a>)</p>
 */
public class CooldownSelectionStrategy<T, X> implements AlternativeSelectionStrategy<T> {

    private static final double INITIAL_WEIGHT = 1.0;
    private static final double COOLDOWN_FACTOR = 0.9;

    private final Random random;
    private final Map<X, Double> weights;
    private final Function<T, X> keyExtractor;

    public CooldownSelectionStrategy(Random random, Function<T, X> keyExtractor) {
        this.random = random;
        this.weights = new HashMap<>();
        this.keyExtractor = keyExtractor;
    }

    @Override
    public T selectAlternative(List<T> elements) {
        if (elements.isEmpty()) throw new IllegalStateException("Can only select alternatives in non empty lists");
        if (elements.size() == 1) return elements.get(0);

        // This is needed if the uniqueness of an element is not defined by the hashCode and equalsMethod but by a key
        Map<X, T> keyToOriginalElementMapping = new HashMap<>();

        // I think we need some "sorted" hash map in order to make this algorithm work
        Map<X, Double> alternativeWeights = new LinkedHashMap<>();

        // initialize weights
        for (T element : elements) {
            X key = keyExtractor.apply(element);
            keyToOriginalElementMapping.put(key, element);
            double weight = weights.computeIfAbsent(key, x -> INITIAL_WEIGHT);
            alternativeWeights.put(key, weight);
        }

        // get maximum weight
        double maxValue = alternativeWeights.values().stream().mapToDouble(x -> x).max().orElseThrow();

        // scale/normalize weights
        alternativeWeights.replaceAll((k, v) -> v / maxValue);

        // choose alternative
        double sum = alternativeWeights.values().stream().mapToDouble(x -> x).sum();

        double randomThreshold = sum * random.nextDouble();
        double upTo = 0.0;
        X selectedKey = null;

        for (Map.Entry<X, Double> xDoubleEntry : alternativeWeights.entrySet()) {
            if (upTo + xDoubleEntry.getValue() > randomThreshold) {
                selectedKey = xDoubleEntry.getKey();
                break;
            }
            upTo += xDoubleEntry.getValue();
        }

        // update weights
        weights.put(selectedKey, COOLDOWN_FACTOR * weights.get(selectedKey));

        return keyToOriginalElementMapping.get(selectedKey);
    }
}
