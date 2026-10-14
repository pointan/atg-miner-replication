package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.ExpressionNodeSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * Objects of this class transform any expression node into a placeholder
 * semantic object. The constructed semantic object is a pure string
 * representation that cannot be interpreted any more. Objects of this class are
 * used as the last processor in the processor pipeline.
 */
public class ExpressionNodeToSemantic implements NodeToSemantic<InterpreterNode, ExpressionNodeSemantic> {

	public ExpressionNodeToSemantic() {
	}

	@Override
	public boolean canProcess(InterpreterNode node) {
		return true;
	}

	@Override
	public ExpressionNodeSemantic process(InterpreterNode node) {
		System.out.println("Could not process: " + node);
		return new ExpressionNodeSemantic(node, node.toString());
	}

}
