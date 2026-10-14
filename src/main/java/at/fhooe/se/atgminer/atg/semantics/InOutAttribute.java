package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>An attribute that is used as in or out parameter of a node.</p>
 */
public class InOutAttribute extends AbstractDataContainerSemantic {

	public enum Type {IN, OUT}

	private BaseSemantic origin;
	private Type type = null;

	private String displayName = null;

	public InOutAttribute(InterpreterNode node, BaseSemantic origin, String name, Type type) {
		this(node, origin, name);
		this.type = type;
	}

	public InOutAttribute(InterpreterNode node, BaseSemantic origin, String name) {
		super(node, name);
		this.origin = origin;
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
		// in out attributes, can only be replaced with other elements, otherwise it stays the same
		return this;
	}

	public BaseSemantic getOrigin() {
		return origin;
	}

	public void setOrigin(BaseSemantic origin) {
		this.origin = origin;
	}

	@Override
	public String toString() {
		if (displayName != null) {
			return displayName;
		} else {
			return getName() + "_" + getId();
		}
//		String arrow = type == null ? "↕" : type == Type.IN ? "↓" : "↑";
//		return arrow + getName()
//				+"(a"+getId()+")"
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean hasDisplayName() {
		return this.displayName != null;
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitInOutAttribute(this);
	}

	public Type getType() {
		return type;
	}
}
