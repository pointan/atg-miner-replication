package at.fhooe.se.atgminer.fuzzer;

import at.fhooe.se.atgminer.atg.ATElement;

/**
 * <p>Interface to calculate expansion costs.</p>
 */
public interface ExpansionCostCalculator {
    /**
     * Calculate the costs internally for a grammar
     */
    void calculateCosts();

    /**
     * returns the costs for a specific grammar element
     *
     * @param element the grammar elements
     * @return the costs of the grammar element
     */
    int getCosts(ATElement element);
}
