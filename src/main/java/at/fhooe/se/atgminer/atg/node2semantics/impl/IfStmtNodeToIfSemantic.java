package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.IfSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.IfStmtNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Converts a {@link IfStmtNode} to an {@link IfSemantic}</p>
 */
public class IfStmtNodeToIfSemantic implements NodeToSemantic<IfStmtNode, IfSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> nodeProcessor;

    public IfStmtNodeToIfSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> nodeProcessor) {
        this.nodeProcessor = nodeProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof IfStmtNode;
    }

    @Override
    public IfSemantic process(IfStmtNode node) {
        return new IfSemantic(
                node,
                nodeProcessor.process(node.getCondition()),
                nodeProcessor.process(node.getThenBlock()),
                node.getElseBlock() == null ? null : nodeProcessor.process(node.getElseBlock())
        );
    }
}
