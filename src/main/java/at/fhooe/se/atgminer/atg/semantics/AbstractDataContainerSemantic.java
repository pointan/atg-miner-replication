package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.atgminer.concept.v2.AttributeName;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.Objects;

/**
 * <p>A base class that contains a variable data element</p>
 */
public abstract class AbstractDataContainerSemantic extends BaseSemantic {
	private final String name;

	public AbstractDataContainerSemantic(InterpreterNode node, String name) {
		super(node);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractDataContainerSemantic)) return false;
		AbstractDataContainerSemantic that = (AbstractDataContainerSemantic) o;
		return AttributeName.removeSubscripts(getName()).equals(AttributeName.removeSubscripts(that.getName()));
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitAbstractDataContainerSemantic(this);
	}
}
