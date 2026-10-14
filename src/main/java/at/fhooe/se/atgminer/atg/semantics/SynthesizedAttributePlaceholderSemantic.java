package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SynthesizedAttributePlaceholderSemantic extends AbstractAttributePlaceholderSemantic {

	private final VariableSemantic formalParam;
	private final BaseSemantic actualParam;

	public SynthesizedAttributePlaceholderSemantic(InterpreterNode node, VariableSemantic formalParam, BaseSemantic actualParam) {
		super(node);
		this.formalParam = Objects.requireNonNull(formalParam);
		this.actualParam = Objects.requireNonNull(actualParam);
	}

	@Override
	public String toString() {
		return String.format("out(%s -> %s)", formalParam, actualParam);
	}

	public String getAttributeName() {
		return formalParam.getName();
	}

	public BaseSemantic getActualParam() {
		return actualParam;
	}

	public VariableSemantic getFormalParam() {
		return formalParam;
	}

	public boolean needAssignmentSemantic() {
		if (actualParam instanceof VariableSemantic) {
			return !formalParam.getName().equals(actualParam.toString());
		} else {
			return true;
		}
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return List.of(formalParam, actualParam);
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		BaseSemantic replacedFormalParam = getFormalParam().replace(replacement);
		if (!(replacedFormalParam instanceof VariableSemantic)) {
			throw new IllegalStateException("Cannot replace formalParam with other than VariableSemantic");
		}
		return new SynthesizedAttributePlaceholderSemantic(getBasedOn(), (VariableSemantic) replacedFormalParam,
				getActualParam().replace(replacement));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SynthesizedAttributePlaceholderSemantic that = (SynthesizedAttributePlaceholderSemantic) o;
		return getFormalParam().equals(that.getFormalParam()) && getActualParam().equals(that.getActualParam());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFormalParam(), getActualParam());
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitSynthesizedAttributePlaceholderSemantic(this);
	}
}
