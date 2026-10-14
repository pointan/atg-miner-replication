package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.GreaterSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.GreaterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Converts a {@link GreaterNode} to {@link GreaterSemantic}</p>
 */
public class GreaterNodeToGreaterSemantic extends AbstractBinaryExpressionNodeToBinaryExprSemantic<GreaterNode, GreaterSemantic>  {
    public GreaterNodeToGreaterSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        super(operandProcessor, GreaterSemantic::new, GreaterNode.class::isInstance);
    }
}
