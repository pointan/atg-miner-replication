package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;

public class EmptySemantic extends BaseSemantic {

	public EmptySemantic(InterpreterNode node) {
		super(node);
	}

	public String toString() {
		return "";
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return List.of();
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		return o.getClass().equals(this.getClass());
	}

	@Override
	public int hashCode() {
		return 48124306;
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitEmptySemantic(this);
	}

}
