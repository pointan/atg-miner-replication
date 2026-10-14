package at.fhooe.se.atgminer.fuzzer;

import at.fhooe.se.atgminer.atg.*;
import at.fhooe.se.atgminer.util.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Implementation on how to expand nodes</p>
 */
public class NodeExpansion {

    public enum CostMode {
        HIGHEST, RANDOM, LOWEST
    }

    private final Random random;
    private final ATGrammar grammar;
    private final ExpansionCostCalculator calculator;
    private final AlternativeSelectionStrategy<ATElement> alternativeSelectionStrategy;

    public NodeExpansion(Random random, ATGrammar grammar, ExpansionCostCalculator calculator, AlternativeSelectionStrategy<ATElement> alternativeSelectionStrategy) {
        this.random = random;
        this.grammar = grammar;
        this.calculator = calculator;
        this.alternativeSelectionStrategy = alternativeSelectionStrategy;
    }

    public List<ATElement> expandNode(DerivationTree tree, CostMode mode) {
        tree.setWasExpanded(true);
        // we need a list here, and cannot use a set, because of the way we implemented elements. e.g. two non-terminal
        // symbols are equal if they have the same "name", but in that case we actually want to have all "same" NT
        // symbols that may occur in different rule.
        List<ATElement> elementsThatWereUsed = new ArrayList<>();


        // Choose which elements are required to expand the node
        tree.getElement().accept(new ThrowingATElementVisitor<ATElement>() {
            @Override
            public ATElement visitNonTerminal(ATNonTerminal symbol) {
                // for the non-terminal we have to search for the corresponding rule
                return grammar.findRuleOf((ATNonTerminal) tree.getElement()).getBody();
            }

            @Override
            public ATElement visitRepetition(ATRepetition element) {
                // for repetitions, we use the element itself
                return element;
            }
        })
        // expand the node
        .accept(new ThrowingATElementVisitor<Void>() {
            @Override
            public Void visitTerminal(ATTerminal symbol) {
                elementsThatWereUsed.add(symbol);
                tree.addChildByElement(symbol);
                return null;
            }

            @Override
            public Void visitTerminalClass(ATTerminalClass symbol) {
                elementsThatWereUsed.add(symbol);
                return this.visitTerminal(symbol);
            }

            @Override
            public Void visitNonTerminal(ATNonTerminal symbol) {
                elementsThatWereUsed.add(symbol);
                tree.addChildByElement(symbol);
                return null;
            }

            @Override
            public Void visitSequence(ATSequence element) {
                elementsThatWereUsed.add(element);
                element.getSeq().forEach(x -> x.accept(this));
                return null;
            }

            @Override
            public Void visitOption(ATOption element) {
                elementsThatWereUsed.add(element);
                switch (mode) {
                    case RANDOM:
                        if (random.nextInt(100) > 50) break;
                    case HIGHEST:
                        return element.getBody().accept(this);
                    case LOWEST:
                    	return null; // case LOWEST omitted, because then we just drop the option
                }
                return null;
            }

            @Override
            public Void visitRepetition(ATRepetition element) {
                elementsThatWereUsed.add(element);
                switch (mode) {
                    case HIGHEST:
                        element.getBody().accept(this);
                        tree.addChildByElement(element);
                        break;
                    case RANDOM:
                        if (random.nextInt(100) > 50) {
                            element.getBody().accept(this);
                            if (random.nextInt(100) > 50) {
                                tree.addChildByElement(element);
                            }
                        }
                        break;
                    case LOWEST:
                        // think if we maybe shouldn't expand it at least once, to increase coverage?
                        break;
                }
                return null;
            }

            @Override
            public Void visitChoice(ATChoice element) {
                elementsThatWereUsed.add(element);
                switch (mode) {
                    case HIGHEST:
                        int max = element.getElements().stream().mapToInt(calculator::getCosts).max().orElseThrow();
                        List<ATElement> elementsWithMaxCosts = element.getElements().stream().filter(x -> calculator.getCosts(x) >= max).collect(Collectors.toList());
                        alternativeSelectionStrategy.selectAlternative(elementsWithMaxCosts).accept(this);
                        break;
                    case RANDOM:
                        alternativeSelectionStrategy.selectAlternative(element.getElements()).accept(this);
                        break;
                    case LOWEST:
                        int min = element.getElements().stream().mapToInt(calculator::getCosts).min().orElseThrow();
                        List<ATElement> elementsWithMinCosts = element.getElements().stream().filter(x -> calculator.getCosts(x) <= min).collect(Collectors.toList());
                        alternativeSelectionStrategy.selectAlternative(elementsWithMinCosts).accept(this);
                        break;
                }

                return null;
            }
        });
        return elementsThatWereUsed;
    }
}
