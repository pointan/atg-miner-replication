package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.ListSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.python.PythonListNode;

import java.util.Arrays;

/**
 * <p>Converts a {@link PythonListNode} to a {@link ListSemantic}</p>
 */
public class PythonListNodeToListSemantic implements NodeToSemantic<PythonListNode, ListSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> listElemProcessor;

    public PythonListNodeToListSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> listElemProcessor) {
        this.listElemProcessor = listElemProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof PythonListNode;
    }

    @Override
    public ListSemantic process(PythonListNode node) {
        return new ListSemantic(node, Arrays.stream(node.getList()).map(listElemProcessor::process).toArray(BaseSemantic[]::new));
    }
}
