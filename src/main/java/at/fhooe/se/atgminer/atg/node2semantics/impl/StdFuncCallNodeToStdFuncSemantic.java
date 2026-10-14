package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.StdFuncSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.func.StdFuncCallNode;

import java.util.Arrays;

/**
 * <p>Converts a {@link StdFuncCallNode} into a {@link StdFuncSemantic}</p>
 */
public class StdFuncCallNodeToStdFuncSemantic implements NodeToSemantic<StdFuncCallNode, StdFuncSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

    public StdFuncCallNodeToStdFuncSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        this.operandProcessor = operandProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof StdFuncCallNode;
    }

    @Override
    public StdFuncSemantic process(StdFuncCallNode node) {
        System.out.println("INFO: Using StdFuncSemantic instead of specified semantic node for function: " + node.getFuncName());
        return new StdFuncSemantic(node, node.getFuncName(), Arrays.stream(node.getParams()).map(operandProcessor::process).toArray(BaseSemantic[]::new));
    }
}
