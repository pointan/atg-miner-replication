package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.ErrorSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ThrowNode;

/**
 * <p>Transforms a {@link ThrowNode} into a {@link ErrorSemantic}</p>
 */
public class ThrowNodeToErrorSemantic implements NodeToSemantic<ThrowNode, ErrorSemantic> {
    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof ThrowNode;
    }

    @Override
    public ErrorSemantic process(ThrowNode node) {
        return new ErrorSemantic(node);
    }
}
