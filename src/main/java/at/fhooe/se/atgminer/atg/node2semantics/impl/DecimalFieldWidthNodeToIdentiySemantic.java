package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.ConstSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.pascal.PascalNodeFactory;
import at.fhooe.se.codeanalysis.adele.interpreter.node.pascal.PascalNodeFactory.DecimalFieldWidthNode;

/**
 * <p>Converts a {@link DecimalFieldWidthNode} to a {@link ConstSemantic}</p>
 */
public class DecimalFieldWidthNodeToIdentiySemantic implements NodeToSemantic<PascalNodeFactory.DecimalFieldWidthNode, BaseSemantic> {

	private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

	public DecimalFieldWidthNodeToIdentiySemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor){
		this.operandProcessor = operandProcessor;
	}

	@Override
	public boolean canProcess(InterpreterNode node) {
		return node instanceof PascalNodeFactory.DecimalFieldWidthNode;
	}

	@Override
	public BaseSemantic process(PascalNodeFactory.DecimalFieldWidthNode node) {
		return operandProcessor.process(node.getParam(0));
	}
}
