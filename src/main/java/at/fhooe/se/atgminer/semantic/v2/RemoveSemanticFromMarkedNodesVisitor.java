package at.fhooe.se.atgminer.semantic.v2;

import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.InAttributePlaceholderSemantic;
import at.fhooe.se.atgminer.atg.semantics.ReturnSemantic;
import at.fhooe.se.atgminer.atg.semantics.SynthesizedAttributePlaceholderSemantic;
import at.fhooe.se.atgminer.parsetree.ParseTree;
import at.fhooe.se.atgminer.parsetree.ParseTreeVisitor;

import java.util.List;
import java.util.Optional;

/**
 * <p>Remove the semantics from marked nodes</p>
 */
public class RemoveSemanticFromMarkedNodesVisitor implements ParseTreeVisitor {

    private static final List<Class<? extends BaseSemantic>> ignoredSemantics = List.of(
            InAttributePlaceholderSemantic.class,
            SynthesizedAttributePlaceholderSemantic.class
    );

    @Override
    public void visit(ParseTree parseTree) {
        parseTree.getChildren().forEach(this::visit); // depth first iteration

        if (parseTree.isAlive()) {
            return;
        }

        if (!parseTree.containsSemantics()) {
            return; // if there are no semantics, we do not have to do anything
        }

        if (parseTree.getParent() == null) {
            System.out.println("WARNING: Node should be removed, but has no parent! Semantics cannot be moved.");
            return;
        }

        // if we have to delete the node, some semantics are no longer necessary, for the moment these are returns and in parameters:
        parseTree.getPreSemantics().removeIf(x -> ignoredSemantics.contains(x.getClass()));
        parseTree.getPostSemantics().removeIf(x -> ignoredSemantics.contains(x.getClass()));

        // Returns nodes are only relevant if parent node is active, so remove if parent node is not active:
        if (!parseTree.getParent().isAlive()) {
            parseTree.getPostSemantics().removeIf(ReturnSemantic.class::isInstance);
        }

        // the following cases arise:
        // if the nodes is not having any siblings, the semantics have to move to the parent (Note: we can not move to child, because if a node is not alive, then its child are not alive too.
        // we can always move to the right, as these nodes have not been visited yet.
        // if we have to move semantics to the left, we have to check if we find a node that is still alive.
        Optional<ParseTree> nextSiblingOpt = parseTree.tryFindNextSibling();
        Optional<ParseTree> prevSiblingOpt = parseTree.tryFindPreviousAliveSibling();
        if (nextSiblingOpt.isPresent()) {
            System.out.println("INFO: Moving semantic to next sibling");
            // move to next
            nextSiblingOpt.get().getPreSemantics().addAll(0, parseTree.getPostSemantics());
            nextSiblingOpt.get().getPreSemantics().addAll(0, parseTree.getPreSemantics());
        } else if (prevSiblingOpt.isPresent()) {
            System.out.println("INFO: Moving semantic to previous sibling");
            prevSiblingOpt.get().getPostSemantics().addAll(parseTree.getPreSemantics());
            prevSiblingOpt.get().getPostSemantics().addAll(parseTree.getPostSemantics());
        } else {
            System.out.println("WARNING: Moving semantics to parent node.");
            parseTree.getParent().getPreSemantics().addAll(parseTree.getPreSemantics());
            parseTree.getParent().getPostSemantics().addAll(0, parseTree.getPostSemantics());
        }

        parseTree.getPreSemantics().clear();
        parseTree.getPostSemantics().clear();
    }
}
