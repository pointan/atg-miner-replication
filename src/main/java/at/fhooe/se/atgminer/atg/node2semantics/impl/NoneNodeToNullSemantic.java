package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.NullSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.python.NoneNode;

/**
 * <p>Converts a {@link NoneNode} into a {@link NullSemantic}</p>
 */
public class NoneNodeToNullSemantic implements NodeToSemantic<NoneNode, NullSemantic> {
    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof NoneNode;
    }

    @Override
    public NullSemantic process(NoneNode node) {
        return new NullSemantic(node);
    }
}
