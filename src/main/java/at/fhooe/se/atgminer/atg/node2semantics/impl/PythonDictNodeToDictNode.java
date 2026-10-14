package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.DictSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.python.PythonDictNode;

/**
 * <p>Converts the {@link PythonDictNode} to a {@link DictSemantic}</p>
 */
public class PythonDictNodeToDictNode implements NodeToSemantic<PythonDictNode, DictSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> elemProcessor;

    public PythonDictNodeToDictNode(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> elemProcessor) {
        this.elemProcessor = elemProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof PythonDictNode;
    }

    @Override
    public DictSemantic process(PythonDictNode node) {
        return new DictSemantic(node, node.getEntries().stream().map(this::mapEntry).toArray(DictSemantic.Entry[]::new));
    }

    private DictSemantic.Entry mapEntry(PythonDictNode.DictNode node) {
        return new DictSemantic.Entry(
                null,
                elemProcessor.process(node.getKey()),
                elemProcessor.process(node.getValue())
        );
    }
}
