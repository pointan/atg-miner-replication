package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.EmptySemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.EmptyStmtNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Transforms an {@link EmptyStmtNode} to an {@link EmptySemantic}.</p>
 */
public class EmptyStmtNodeToEmptySemantic implements NodeToSemantic<EmptyStmtNode, EmptySemantic> {
    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof EmptyStmtNode;
    }

    @Override
    public EmptySemantic process(EmptyStmtNode node) {
        return new EmptySemantic(node);
    }
}
