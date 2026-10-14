package at.fhooe.se.atgminer.atg.node2semantics.impl;

import java.util.ArrayList;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.SetConstructorSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.SetConstructorNode;

/**
 * Converts {@link SetConstructorNode} to {@link SetConstructorSemantic} .
 */
public class SetConstructorNodeToSetConstructorSemantic
		implements NodeToSemantic<SetConstructorNode, SetConstructorSemantic> {

	private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

	public SetConstructorNodeToSetConstructorSemantic(
			NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
		this.operandProcessor = operandProcessor;
	}

	@Override
	public boolean canProcess(InterpreterNode node) {
		return node instanceof SetConstructorNode;
	}

	@Override
	public SetConstructorSemantic process(SetConstructorNode node) {
		var l = new ArrayList<BaseSemantic>();
		for (var e : node.getLiterals()) {
			l.add(operandProcessor.process(e));
		}
		return new SetConstructorSemantic(node, l);
	}
}
