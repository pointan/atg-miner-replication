package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.InputSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.python.InputNode;

/**
 * <p>Converts a {@link InputNode} to an {@link InputSemantic}</p>
 */
public class InputNodeToInputSemantic implements NodeToSemantic<InputNode, InputSemantic> {
    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof InputNode;
    }

    @Override
    public InputSemantic process(InputNode node) {
        return new InputSemantic(node);
    }
}
