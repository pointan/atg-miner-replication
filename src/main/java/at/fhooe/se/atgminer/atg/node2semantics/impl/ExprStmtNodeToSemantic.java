package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExprStmtNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Unpacks the {@link ExprStmtNode} and uses the pipeline to create a suitable semantic node.</p>
 */
public class ExprStmtNodeToSemantic implements NodeToSemantic<ExprStmtNode, BaseSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

    public ExprStmtNodeToSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        this.operandProcessor = operandProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof ExprStmtNode;
    }

    @Override
    public BaseSemantic process(ExprStmtNode node) {
        return operandProcessor.process(node.getExpression());
    }
}
