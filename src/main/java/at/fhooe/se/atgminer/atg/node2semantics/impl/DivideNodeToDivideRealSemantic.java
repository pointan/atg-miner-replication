package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.DivideRealSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.DivideNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Converts an {@link DivideNode} to a {@link DivideRealSemantic}</p>
 */
public class DivideNodeToDivideRealSemantic extends AbstractBinaryExpressionNodeToBinaryExprSemantic<DivideNode, DivideRealSemantic> {
    public DivideNodeToDivideRealSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        super(operandProcessor, DivideRealSemantic::new, DivideNode.class::isInstance);
    }
}
