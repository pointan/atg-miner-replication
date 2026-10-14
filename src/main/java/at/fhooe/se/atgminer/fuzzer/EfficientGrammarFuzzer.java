package at.fhooe.se.atgminer.fuzzer;

import at.fhooe.se.atgminer.atg.ATElement;
import at.fhooe.se.atgminer.atg.ATGrammar;
import at.fhooe.se.atgminer.atg.ATNonTerminal;
import at.fhooe.se.atgminer.atg.ATTerminal;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Efficient Grammar Fuzzer based on the Grammar Fuzzer introduce in FuzzingBook</p>
 *
 * @see <a
 * href="https://www.fuzzingbook.org/html/GrammarFuzzer.html">https://www.fuzzingbook.org/html/GrammarFuzzer.html</a>
 */
public class EfficientGrammarFuzzer {
    private static final int CLOSING_LIMIT = 100_000;
    private final ATGrammar grammar;
    private int minNts = 3;
    private int maxNts = 10;

    private String delimiter = "";

    private final AlternativeSelectionStrategy<DerivationTree> selectNodeToExpand;
    private final NodeExpansion expansion;

    private List<ATElement> lastUsedElementsForInputGeneration;

    public EfficientGrammarFuzzer(ATGrammar grammar, AlternativeSelectionStrategy<DerivationTree> selectNodeToExpand, NodeExpansion nodeExpansion) {
        this.grammar = grammar;
        this.selectNodeToExpand = selectNodeToExpand;
        this.expansion = nodeExpansion;
    }

    public String generateInput() {
        lastUsedElementsForInputGeneration = new ArrayList<>();
        // Start create a tree with the start element
        DerivationTree dt = new DerivationTree(grammar.getStartSymbol());

        // expand the most expensive nodes until we reach minNTs
        List<DerivationTree> expandableNodes;
        while (!(expandableNodes = collectExpandableNodes(dt)).isEmpty() && countNtsInTree(dt) < minNts) {
            // select a node that should be expanded:
            DerivationTree nextNodeToExpand = selectNodeToExpand.selectAlternative(expandableNodes);
            List<ATElement> atElements = expansion.expandNode(nextNodeToExpand, NodeExpansion.CostMode.HIGHEST);
            lastUsedElementsForInputGeneration.addAll(atElements);
        }

        // expand randomly until we reach maxNTs
        while (!(expandableNodes = collectExpandableNodes(dt)).isEmpty() && countNtsInTree(dt) < maxNts) {
            // select a node that should be expanded:
            DerivationTree nextNodeToExpand = selectNodeToExpand.selectAlternative(expandableNodes);
            List<ATElement> atElements = expansion.expandNode(nextNodeToExpand, NodeExpansion.CostMode.RANDOM);
            lastUsedElementsForInputGeneration.addAll(atElements);
        }

        // close the expansion by using the elements with min cost
        int i = 0;
        while (!(expandableNodes = collectExpandableNodes(dt)).isEmpty()) {
            // select a node that should be expanded:
            DerivationTree nextNodeToExpand = selectNodeToExpand.selectAlternative(expandableNodes);
            List<ATElement> atElements = expansion.expandNode(nextNodeToExpand, NodeExpansion.CostMode.LOWEST);
            lastUsedElementsForInputGeneration.addAll(atElements);
            if (i++ > CLOSING_LIMIT)
                throw new IllegalStateException("Exceeded number of operations for closing");
        }

        // collect the leaves and concat them into the input stream
        return dt.stream()
                .filter(t -> t.getChildren().isEmpty())
                .map(DerivationTree::getElement)
                .filter(ATTerminal.class::isInstance)
                .map(ATTerminal.class::cast)
                .map(ATTerminal::getToken)
                .collect(Collectors.joining(delimiter));
    }

    private List<DerivationTree> collectExpandableNodes(DerivationTree start) {
        return start.stream().filter(DerivationTree::canExpand).collect(Collectors.toList());
    }

    private int countNtsInTree(DerivationTree tree) {
        return (int) tree.stream().map(DerivationTree::getElement).filter(ATNonTerminal.class::isInstance).count();
    }

    public void setMinNts(int minNts) {
        this.minNts = minNts;
    }

    public void setMaxNts(int maxNts) {
        this.maxNts = maxNts;
    }

    public List<ATElement> getLastUsedElementsForInputGeneration() {
        return Collections.unmodifiableList(lastUsedElementsForInputGeneration);
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
