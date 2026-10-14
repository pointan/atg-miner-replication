package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BlockSemantic extends BaseSemantic {

	private final List<BaseSemantic> semanticActions;

	public BlockSemantic() {
		this(null, new ArrayList<>());
	}

	public BlockSemantic(InterpreterNode node) {
		this(node, new ArrayList<>());
	}

	public BlockSemantic(List<BaseSemantic> semanticActions) {
		this(null, semanticActions);
	}

	public BlockSemantic(InterpreterNode node, List<BaseSemantic> semanticActions) {
		super(node);
		this.semanticActions = semanticActions;
	}

	@Override
	public String toString() {
		return semanticActions.stream().map(Object::toString).collect(Collectors.joining("; "));
	}

	public void addSemantic(BaseSemantic s) {
		this.semanticActions.add(s);
	}

	public void addSemantic(int index, BaseSemantic s) {
		this.semanticActions.add(index, s);
	}

	public boolean removeSemantics(BaseSemantic s) {
		return this.semanticActions.remove(s);
	}

	public List<BaseSemantic> getSemantics() {
		return new ArrayList<>(semanticActions);
	}

	@Override
	public List<BaseSemantic> getChildren() {
		return semanticActions;
	}

	@Override
	public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
		if (replacement.containsKey(this)) {
			return replacement.get(this);
		}
		BlockSemantic blockSemantic = new BlockSemantic(getBasedOn());
		getSemantics().stream().map(sem -> sem.replace(replacement)).forEach(blockSemantic::addSemantic);
		return blockSemantic;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BlockSemantic that = (BlockSemantic) o;
		return semanticActions.equals(that.semanticActions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(semanticActions);
	}

	public boolean isEmpty() {
		return semanticActions.isEmpty();
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitBlockSemantic(this);
	}
}
