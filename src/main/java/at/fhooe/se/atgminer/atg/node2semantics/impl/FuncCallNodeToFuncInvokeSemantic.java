package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.FuncInvokeSemantic;
import at.fhooe.se.atgminer.atg.semantics.ResultPlaceholderSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.FuncCallNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.MethodInvokeNode;

import java.util.stream.Stream;

/**
 * <p>Converts a {@link FuncCallNode} into a {@link ResultPlaceholderSemantic}</p>
 */
public class FuncCallNodeToFuncInvokeSemantic implements NodeToSemantic<FuncCallNode, BaseSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

    public FuncCallNodeToFuncInvokeSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        this.operandProcessor = operandProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof FuncCallNode;
    }

    @Override
    public BaseSemantic process(FuncCallNode node) {
        return new FuncInvokeSemantic(node, node.getFuncName(), Stream.of(node.getParams()).map(operandProcessor::process).toArray(BaseSemantic[]::new));
    }
}
