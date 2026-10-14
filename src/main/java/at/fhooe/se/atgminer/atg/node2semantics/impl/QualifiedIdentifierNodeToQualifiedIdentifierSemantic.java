package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.QualifiedIdentifierSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.QualifiedIdentifierNode;

/**
 * <p>Converts {@link QualifiedIdentifierNode} to {@link QualifiedIdentifierSemantic}</p>
 */
public class QualifiedIdentifierNodeToQualifiedIdentifierSemantic implements NodeToSemantic<QualifiedIdentifierNode, QualifiedIdentifierSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

    public QualifiedIdentifierNodeToQualifiedIdentifierSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        this.operandProcessor = operandProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof QualifiedIdentifierNode;
    }

    @Override
    public QualifiedIdentifierSemantic process(QualifiedIdentifierNode node) {
        return new QualifiedIdentifierSemantic(node, operandProcessor.process(node.getQualifiers()), operandProcessor.process(node.getMember()));
    }
}
