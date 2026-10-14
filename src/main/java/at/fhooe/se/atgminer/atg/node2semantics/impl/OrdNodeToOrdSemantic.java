package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.OrdSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.pascal.OrdNode;

/**
 * <p>Converts {@link OrdNode} to {@link OrdSemantic}</p>
 */
public class OrdNodeToOrdSemantic implements NodeToSemantic<OrdNode, OrdSemantic> {

	private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

	public OrdNodeToOrdSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		this.operandProcessor = operandProcessor;
	}

	@Override
	public boolean canProcess(InterpreterNode node) {
		return node instanceof OrdNode;
	}

	@Override
	public OrdSemantic process(OrdNode node) {
		return new OrdSemantic(node, operandProcessor.process(node.getParam(0)));
	}
}
