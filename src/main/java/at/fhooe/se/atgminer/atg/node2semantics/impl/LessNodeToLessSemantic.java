package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.LessSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.LessNode;

/**
 * <p>Converts {@link LessNode} to {@link LessSemantic}</p>
 */
public class LessNodeToLessSemantic extends AbstractBinaryExpressionNodeToBinaryExprSemantic<LessNode, LessSemantic> {
    public LessNodeToLessSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        super(operandProcessor, LessSemantic::new, LessNode.class::isInstance);
    }
}
