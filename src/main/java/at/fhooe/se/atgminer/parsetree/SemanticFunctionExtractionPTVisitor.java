package at.fhooe.se.atgminer.parsetree;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemanticPipeline;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.DefSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SemanticFunctionExtractionPTVisitor implements ParseTreeVisitor {

    private final NodeToSemantic<InterpreterNode, BaseSemantic> converter = NodeToSemanticPipeline.createPipelineWithoutFunctionPlaceholder();

    private final List<DefSemantic> globalMethods = new ArrayList<>();

    @Override
    public void visit(ParseTree parseTree) {
        // iterate the tree:
        parseTree.getChildren().forEach(this::visit); // iterate first, to make sure to get transitive method also.

        // only apply to possible NT nodes:
        // only apply to nodes without terminals:
        if (!(parseTree.isTerminalSymbol() || parseTree.isPseudoNode() || parseTree.isSemOnlyNode() || parseTree.containsTerminals())) {
            // pre-semantics can be ignored in this case. the only pre-semantic that could occur is a placeholder for an inherited parameter, which shall not be used here
            // so first we replace the semantics, with new semantics:
            Map<BaseSemantic, BaseSemantic> replacementMap = parseTree.getPostSemantics().stream().collect(Collectors.toMap(Function.identity(), x -> converter.process(x.getBasedOn())));
            replacementMap.forEach((oldSem, newSem) -> {
                List<BaseSemantic> postSemantics = parseTree.getPostSemantics();
                postSemantics.set(postSemantics.indexOf(oldSem), newSem);
            });

            DefSemantic defSemantic = (DefSemantic) converter.process(parseTree.getBasedOn());
            globalMethods.add(defSemantic);

            // finally, make the node for removal and move the semantics to the previous node:
            ParseTree prevPT = parseTree.tryFindPreviousAliveSibling().orElseThrow();
            prevPT.getPostSemantics().addAll(parseTree.getPostSemantics());
            parseTree.getPostSemantics().clear();
            parseTree.getPreSemantics().clear();
            parseTree.getChildren().clear();
            parseTree.setAlive(false);
            parseTree.setRemovable(true);
        }
    }

    public List<DefSemantic> getGlobalMethods() {
        return globalMethods;
    }
}
