package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.AssignmentSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.python.PythonAssignNode;

/**
 * <p>Converts {@link PythonAssignNode} to {@link AssignmentSemantic}</p>
 */
public class PythonAssignNodeToAssignSemantic implements NodeToSemantic<PythonAssignNode, AssignmentSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

    public PythonAssignNodeToAssignSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        this.operandProcessor = operandProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof PythonAssignNode;
    }

    @Override
    public AssignmentSemantic process(PythonAssignNode node) {
        return new AssignmentSemantic(node, operandProcessor.process(node.getLhs()), operandProcessor.process(node.getRhs()));
    }
}
