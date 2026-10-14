package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.MethodInvokeSemantic;
import at.fhooe.se.atgminer.atg.semantics.ResultPlaceholderSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.IdentifierNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.MethodInvokeNode;

import java.util.List;
import java.util.stream.Stream;

/**
 * <p>Converts a {@link MethodInvokeNode} into a {@link ResultPlaceholderSemantic}</p>
 */
public class MethodInvokeNodeToResultPlaceholder implements NodeToSemantic<MethodInvokeNode, BaseSemantic> {

    private static final List<String> resultPlaceholderIdentifiers = List.of("self");

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

    public MethodInvokeNodeToResultPlaceholder(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        this.operandProcessor = operandProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof MethodInvokeNode;
    }

    @Override
    public BaseSemantic process(MethodInvokeNode node) {
        if (node.getObjectNode() instanceof IdentifierNode && resultPlaceholderIdentifiers.contains(((IdentifierNode) node.getObjectNode()).getIdent())) {
            return new ResultPlaceholderSemantic(node, node.getMethodName());
        }
        return new MethodInvokeSemantic(node, operandProcessor.process(node.getObjectNode()), node.getMethodName(), Stream.of(node.getParams()).map(operandProcessor::process).toArray(BaseSemantic[]::new));
    }
}
