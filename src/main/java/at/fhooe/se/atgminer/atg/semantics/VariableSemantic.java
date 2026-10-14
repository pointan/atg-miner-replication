package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;

/**
 * <p>A class that represents a variable</p>
 */
public class VariableSemantic extends AbstractDataContainerSemantic {

	private final boolean inLocalScope;

	public VariableSemantic(InterpreterNode node, String name) {
		this(node, name, false);
	}

	public VariableSemantic(InterpreterNode node, String name, boolean inLocalScope) {
		super(node, name);
		this.inLocalScope = inLocalScope;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return List.of();
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		return new VariableSemantic(getBasedOn(), getName(), isInLocalScope());
	}

	public boolean isInLocalScope() {
			return inLocalScope;
		}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitVariableSemantic(this);
	}
}