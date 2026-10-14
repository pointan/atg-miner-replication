package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.WhileSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.WhileStmtNode;

/**
 * <p>Converts a {@link WhileStmtNode} to a {@link WhileSemantic}</p>
 */
public class WhileStmtNodeToWhileSemantic implements NodeToSemantic<WhileStmtNode, WhileSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

    public WhileStmtNodeToWhileSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        this.operandProcessor = operandProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof WhileStmtNode;
    }

    @Override
    public WhileSemantic process(WhileStmtNode node) {
        return new WhileSemantic(node, operandProcessor.process(node.getCondition()), operandProcessor.process(node.getBody()));
    }
}
