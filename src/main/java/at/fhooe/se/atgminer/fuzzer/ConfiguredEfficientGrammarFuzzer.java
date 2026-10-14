package at.fhooe.se.atgminer.fuzzer;

import java.util.*;
import java.util.stream.Collectors;

import at.fhooe.se.atgminer.atg.*;
import at.fhooe.se.atgminer.fuzzer.impl.CooldownSelectionStrategy;
import at.fhooe.se.atgminer.fuzzer.impl.CoverageSelectionStrategy;
import at.fhooe.se.atgminer.fuzzer.impl.GrammarExpansionCostCalculator;
import at.fhooe.se.atgminer.fuzzer.impl.RandomAlternativeSelectionStrategy;
import at.fhooe.se.atgminer.util.Random;

/**
 * <p>This is a fully configured grammar fuzzer based on {@link EfficientGrammarFuzzer}</p>
 */
public class ConfiguredEfficientGrammarFuzzer {

    private final ATGrammar grammar;
    private final EfficientGrammarFuzzer efficientGrammarFuzzer;
    private final boolean special;
    private final Random random = new Random(42);

    public ConfiguredEfficientGrammarFuzzer(ATGrammar grammar, String delimiter) {
        this(grammar);
        efficientGrammarFuzzer.setDelimiter(delimiter);
    }

    public ConfiguredEfficientGrammarFuzzer(ATGrammar grammar) {
        this.grammar = grammar;
        // calculate the number of non-terminals inside the grammar. we use that information to calculate fuzzer settings
        int numberOfNts = (int) grammar.getRuleList().stream().map(ATRule::getBody).flatMap(ATElement::stream).filter(ATNonTerminal.class::isInstance).count();

        // calculate grammar costs
        GrammarExpansionCostCalculator grammarExpansionCostCalculator = new GrammarExpansionCostCalculator(grammar);
        grammarExpansionCostCalculator.calculateCosts();
        grammarExpansionCostCalculator.printExpansionCosts();

        // initialize selection strategy for options
        AlternativeSelectionStrategy<ATElement> nodeExpansionAlternativeSelectionStrategy = new CoverageSelectionStrategy<>(new CooldownSelectionStrategy<>(random, ATElement::getID), ATElement::getID);

        // initialize selection strategy for derivation trees
        AlternativeSelectionStrategy<DerivationTree> selectNodeToExpandStrategy = new RandomAlternativeSelectionStrategy<>(random);

        // initialize node expansion
        NodeExpansion expansion = new NodeExpansion(random, grammar, grammarExpansionCostCalculator, nodeExpansionAlternativeSelectionStrategy);

        // initialize the fuzzer itself with the expansions strategies
        efficientGrammarFuzzer = new EfficientGrammarFuzzer(grammar, selectNodeToExpandStrategy, expansion);
        int minNts, maxNts;
        if (grammarExpansionCostCalculator.containsRecursion()) {
            System.out.println("Grammar contains infinity costs.");
            minNts = (int) (numberOfNts * 2.5);
            maxNts = Math.max(minNts * 2, 3);
            special = false;
        } else {
            System.out.println("Grammar does not contain infinity costs.");
            minNts = 0;
            maxNts = 20;
            special = true;
        }

        efficientGrammarFuzzer.setMinNts(minNts);
        efficientGrammarFuzzer.setMaxNts(maxNts);
        System.out.println("Setting min nts to " + minNts + " and max to " + maxNts);
    }

    public String generateInput() {
        return efficientGrammarFuzzer.generateInput();
    }

    public void markLastUsedGrammarElements() {
        efficientGrammarFuzzer.getLastUsedElementsForInputGeneration().forEach(x -> x.setWasUsed(true));
    }

    public class GeneratedInputs {
        private final long elementsToCover;
        private final int limit;
        private long currentAmountOfElementsCovered = 0;
        private int i;

        private GeneratedInputs(int limit) {
            elementsToCover = grammar.getRuleList().stream().map(ATRule::getBody).flatMap(ATElement::stream).count();
            this.limit = limit;
            i = 0;
        }

        public boolean hasMore() {
            System.out.printf("INFO: Can construct more. Current index %d with limit %d. Currently covered %d with limit %d%n", i, limit, currentAmountOfElementsCovered, elementsToCover);
            return i < limit && currentAmountOfElementsCovered < elementsToCover;
        }

        public String nextInput() {
            if (!hasMore()) {
                throw new IllegalStateException("No more elements");
            }
            return generateInput();
        }

        public void commitLastInput() {
            i++;
            markLastUsedGrammarElements();
            currentAmountOfElementsCovered = grammar.getRuleList().stream().map(ATRule::getBody).flatMap(ATElement::stream).filter(ATElement::isWasUsed).count();
        }
    }

    public GeneratedInputs generateInputsToCoverFullGrammarWithGenerator(int limit) {
        if (special) {
            return new GeneratedInputsSpecial(expand(grammar, grammar.getStartSymbol()));
        } else {
            return new GeneratedInputs(limit);
        }
//        return new GeneratedInputs(limit);
    }

    public List<String> generateInputsToCoverFullGrammar(int limit) {
        List<String> inputs = new ArrayList<>();
        long currentAmountOfElementsCovered = 0;
        long elementsToCover = grammar.getRuleList().stream().map(ATRule::getBody).flatMap(ATElement::stream).count();
        for (int i = 0; i < limit && currentAmountOfElementsCovered < elementsToCover; i++) {
            try {
                String input = generateInput();
                inputs.add(input);
                markLastUsedGrammarElements();
                currentAmountOfElementsCovered = grammar.getRuleList().stream().map(ATRule::getBody).flatMap(ATElement::stream).filter(ATElement::isWasUsed).count();
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Generated " + inputs.size() + " and covered " + currentAmountOfElementsCovered + " elements of " + elementsToCover);
        return inputs;
    }

    public class GeneratedInputsSpecial extends GeneratedInputs {

        Iterator<String> it;

        private GeneratedInputsSpecial(List<String> inputs) {
            super(0);
            it = inputs.iterator();
        }

        public boolean hasMore() {
            return it.hasNext();
        }

        public String nextInput() {
            return it.next();
        }

        public void commitLastInput() {

        }
    }

    private List<String> expand(ATGrammar grammar, ATElement element) {
        Set<ATElement> seenElements = new HashSet<>();
        return element.accept(new ThrowingATElementVisitor<>() {
            @Override
            public List<String> visitTerminal(ATTerminal symbol) {
                return List.of(symbol.getToken());
            }

            @Override
            public List<String> visitTerminalClass(ATTerminalClass symbol) {
                return List.of(symbol.getToken());
            }

            @Override
            public List<String> visitNonTerminal(ATNonTerminal symbol) {
                if (symbol.getKey().equalsIgnoreCase("any")) {
                    var elems = grammar.findRuleOf(symbol).getBody().getElements();
                    return elems.get(random.nextInt(elems.size())).accept(this);
                }
                return grammar.findRuleOf(symbol).getBody().accept(this);
            }

            @Override
            public List<String> visitSequence(ATSequence element) {
                List<List<String>> collect = element.getSeq().stream().map(x -> x.accept(this)).collect(Collectors.toList());
                return combinationsCartesianProduct(collect);
            }

            @Override
            public List<String> visitOption(ATOption element) {
                return element.getBody().accept(this);
            }

            @Override
            public List<String> visitRepetition(ATRepetition element) {
                if (seenElements.add(element)) {
                    return element.getBody().accept(this);
                } else {
                    return element.getBody().accept(this)
                            .stream()
                            .map(s -> s.repeat(3))
                            .collect(Collectors.toList());
                }
            }

            @Override
            public List<String> visitChoice(ATChoice element) {
                if (seenElements.add(element)) {
                    return element.getElements().stream().flatMap(x -> x.accept(this).stream()).collect(Collectors.toList());
                } else {
                    return element.getElements().get(0).accept(this);
                }
            }

            @Override
            public List<String> visitTerminalGroup(ATTerminalGroup group) {
                throw new UnsupportedOperationException("not yet supported!");
            }
        });
    }

    private static List<String> combinationsCartesianProduct(List<List<String>> input) {
//        System.out.println("Building all combinations for: " + input);
        List<String> result = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            return result;
        }
        buildCombinationsCartesianProduct(input, 0, "", result);
        return result;
    }

    private static void buildCombinationsCartesianProduct(List<List<String>> input, int index, String current, List<String> result) {
        if (index == input.size()) {
            result.add(current);
            return;
        }

        for (String item : input.get(index)) {
            buildCombinationsCartesianProduct(input, index + 1, current + item, result);
        }
    }
}
