package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.ArrayAccessSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ArrayAccessNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Arrays;

/**
 * <p>Converts an {@link ArrayAccessNode} to an {@link ArrayAccessSemantic}</p>
 */
public class ArrayAccessNodeToArrayAccessSemantic implements NodeToSemantic<ArrayAccessNode, ArrayAccessSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> nodeProcessor;

    public ArrayAccessNodeToArrayAccessSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> nodeProcessor) {
        this.nodeProcessor = nodeProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof ArrayAccessNode;
    }

    @Override
    public ArrayAccessSemantic process(ArrayAccessNode node) {
        return new ArrayAccessSemantic(
                node,
                nodeProcessor.process(node.getArrayName()),
                Arrays.stream(node.getSubscripts()).map(nodeProcessor::process).toArray(BaseSemantic[]::new)
        );
    }
}
