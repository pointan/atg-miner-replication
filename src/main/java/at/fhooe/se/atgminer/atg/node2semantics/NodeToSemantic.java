package at.fhooe.se.atgminer.atg.node2semantics;

import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Interface which defines how to convert derivations of {@link ExpressionNode} into a the corresponding
 * {@link BaseSemantic} element</p>
 */
public interface NodeToSemantic<N extends InterpreterNode, S extends BaseSemantic> {
    /**
     * Method that checks if a given expression node, can be processed by this method. if so it is safe to call the
     * process method
     *
     * @param node the node that should be checked
     * @return true if the process method can be called with that node, otherwise false
     */
    boolean canProcess(InterpreterNode node);

    /**
     * The method that performs the transformation from the {@link ExpressionNode} to the {@link BaseSemantic}
     *
     * @param node the {@link ExpressionNode} that should be converted
     * @return the converted {@link BaseSemantic} element
     */
    S process(N node);
}
