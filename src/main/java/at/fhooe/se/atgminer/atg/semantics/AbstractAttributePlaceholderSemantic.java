package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Base class which acts as a placeholder for attributes</p>
 */
public abstract class AbstractAttributePlaceholderSemantic extends BaseSemantic {
	public AbstractAttributePlaceholderSemantic(InterpreterNode basedOn) {
		super(basedOn);
	}

	public abstract BaseSemantic getActualParam();
	public abstract VariableSemantic getFormalParam();

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitAbstractAttributePlaceholderSemantic(this);
	}
}
