package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Placeholder for input arguments</p>
 */
public class InAttributePlaceholderSemantic extends AbstractAttributePlaceholderSemantic {
	private final VariableSemantic formalParam;
	private final BaseSemantic actualParam;

	public InAttributePlaceholderSemantic(InterpreterNode node, VariableSemantic formalParam, BaseSemantic actualParam) {
		super(node);
		this.formalParam = Objects.requireNonNull(formalParam);
		this.actualParam = Objects.requireNonNull(actualParam);
	}

	public VariableSemantic getFormalParam() {
		return formalParam;
	}

	public BaseSemantic getActualParam() {
		return actualParam;
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return List.of(formalParam, actualParam);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		InAttributePlaceholderSemantic that = (InAttributePlaceholderSemantic) o;
		return getFormalParam().equals(that.getFormalParam()) && getActualParam().equals(that.getActualParam());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFormalParam(), getActualParam());
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}

		// It is probably not useful to replace the formal parameter
//		BaseSemantic replacedFormalParam = getFormalParam().replace(replacement);
//		if (!(replacedFormalParam instanceof AbstractDataContainerSemantic)) {
//			throw new IllegalStateException("Cannot replace formalParam with other than VariableSemantic");
//		}
		return new InAttributePlaceholderSemantic(getBasedOn(), formalParam, getActualParam().replace(replacement));
	}

	public String toString() {
		return String.format("in(%s -&gt; %s)", actualParam, formalParam);
	}


	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitInAttributePlaceholderSemantic(this);
	}
}
