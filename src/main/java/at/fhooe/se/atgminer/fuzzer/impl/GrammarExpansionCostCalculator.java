package at.fhooe.se.atgminer.fuzzer.impl;

import at.fhooe.se.atgminer.atg.*;
import at.fhooe.se.atgminer.atg.visitor.ToStringVisitor;
import at.fhooe.se.atgminer.concept.v2.AttributeName;
import at.fhooe.se.atgminer.fuzzer.ExpansionCostCalculator;
import at.fhooe.se.atgminer.util.BashFormatter;

import java.util.*;

/**
 * <p>Calculates the cost of the grammar when expanding it.</p>
 */
public class GrammarExpansionCostCalculator implements ExpansionCostCalculator {

    /**
     * In theory the double data type provides a real infinity in java, but incrementing infinity still stays infinity
     * if we have a very high number we can increment these values and can distinguish between infinity and infinity + 1
     */
    static final int PSEUDO_INFINITY = 1000;

    private final ATGrammar grammar;
    private final Map<Integer, Integer> costsTable;

    private boolean containsRecursion = false;

    public GrammarExpansionCostCalculator(ATGrammar grammar) {
        this.grammar = grammar;
        this.costsTable = new HashMap<>();
    }

    @Override
    public void calculateCosts() {
        for (ATRule atRule : grammar.getRuleList()) {
            Deque<ATNonTerminal> ntSeen = new ArrayDeque<>();
            ntSeen.push(atRule.getKey());
            costsTable.put(atRule.getKey().getID(), calcCosts(atRule.getBody(), ntSeen));
        }
    }

    private int calcCosts(ATElement element, Deque<ATNonTerminal> ntSeen) {
        int val = element.accept(new ThrowingATElementVisitor<>() {
            @Override
            public Integer visitTerminal(ATTerminal symbol) {
                return 1;
            }

            @Override
            public Integer visitTerminalClass(ATTerminalClass symbol) {
                return 1;
            }

            @Override
            public Integer visitNonTerminal(ATNonTerminal symbol) {
                if (ntSeen.contains(symbol)) {
                    containsRecursion = true;
                    return PSEUDO_INFINITY;
                }
                ntSeen.push(symbol);
                int res = 0;
                try {
                    res = calcCosts(grammar.findRuleOf(symbol).getBody(), ntSeen) + 1;
                } catch(NullPointerException ne) {
                    System.out.println();
                }
                ntSeen.pop();
                return res;
            }

            @Override
            public Integer visitSequence(ATSequence element) {
                return element.getSeq().stream().mapToInt(elem -> calcCosts(elem, ntSeen)).sum();
            }

            @Override
            public Integer visitOption(ATOption element) {
                // we still calculate the score of the elements inside the option part, but the option itself can produce zero costs.
                calcCosts(element.getBody(), ntSeen);
                return 0;
            }

            @Override
            public Integer visitRepetition(ATRepetition element) {
                /*
                  Costs of a repetition element:
                  When we think about the following example, that contains a repetition.
                  START = ('x' { 'x' }) | ('y' 'z') .
                  We could also write that down with recursion:
                  START₁ = X₁ | ('y'₁ 'z'₁)₂ .
                  X₁ = 'x'₁ | ('x'₁ X∞)∞ .
                  That result in having a branch in x being ∞, whilst the other branch is 1, leading to X having
                  the overall result 1. Nevertheless if we expand X we can expand it with much higher costs. That means
                  we cannot say that the costs of the repetition is just 1, but also cannot say that it is ∞.
                  I think the best option is to just see them as the optional path, and use costs of 0.
                 */
                calcCosts(element.getBody(), ntSeen); // but we still calculate the scores of the elements inside the repetition
                return 0;
            }

            @Override
            public Integer visitChoice(ATChoice element) {
                return element.getElements().stream()
                        // We have to use an own ntSeen for each path, because otherwise we may visit non-terminal, that
                        // are actually not part of the current possible expansion
                        .mapToInt(elem -> calcCosts(elem, ntSeen))
                        .min()
                        .orElseThrow();
            }
        });

        if (!costsTable.containsKey(element.getID()) || costsTable.get(element.getID()) > val) {
            costsTable.put(element.getID(), val);
        }
        return val;
    }

    public void printExpansionCosts() {
        System.out.println(grammar.accept(new ToStringVisitor(new BashFormatter(false)) {
            @Override
            public String visitTerminal(ATTerminal symbol) {
                return super.visitTerminal(symbol) + AttributeName.replaceSubscripts(String.valueOf(costsTable.get(symbol.getID())));
            }

            @Override
            public String visitNonTerminal(ATNonTerminal symbol) {
                return super.visitNonTerminal(symbol) + AttributeName.replaceSubscripts(String.valueOf(costsTable.get(symbol.getID())));
            }

            @Override
            public String visitSequence(ATSequence element) {
                return super.visitSequence(element) + AttributeName.replaceSubscripts(String.valueOf(costsTable.get(element.getID())));
            }
        }));
    }

    @Override
    public int getCosts(ATElement element) {
        return costsTable.get(element.getID());
    }

    public boolean containsRecursion() {
        return containsRecursion;
    }

}
