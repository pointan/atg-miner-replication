package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.ReturnSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ReturnStmtNode;

public class ReturnStmtNodeToReturnSemantic implements NodeToSemantic<ReturnStmtNode, ReturnSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> nodeProcessor;

    public ReturnStmtNodeToReturnSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> nodeProcessor) {
        this.nodeProcessor = nodeProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof ReturnStmtNode;
    }

    @Override
    public ReturnSemantic process(ReturnStmtNode node) {
        return new ReturnSemantic(node, nodeProcessor.process(node.getReturnValue()));
    }
}
