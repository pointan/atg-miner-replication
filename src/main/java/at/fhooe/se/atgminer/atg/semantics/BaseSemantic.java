package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p>
 * Base class to represent semantic objects
 * </p>
 */
public abstract class BaseSemantic implements Iterable<BaseSemantic> {
	private static int nextId = 1;
	private final int id;

	private final InterpreterNode basedOn;

	public BaseSemantic(InterpreterNode basedOn) {
		this.id = nextId++;
		this.basedOn = basedOn;
	}

	public abstract List<BaseSemantic> getChildren();

	@Override
	public Iterator<BaseSemantic> iterator() {
		return new BaseSemanticIterator(this);
	}

	public Stream<BaseSemantic> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	public <T> Stream<T> filteredStream(Class<T> clazz) {
		return stream().filter(clazz::isInstance).map(clazz::cast);
	}

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract String toString();

	public static String toString(List<BaseSemantic> list) {
		StringJoiner str = new StringJoiner("; ");
		list.forEach((e) -> str.add(e.toString()));
		return str.toString();
	}

	/**
	 * This function recursively replaces all nodes in the replacement mapping. if
	 * the current node is not in the replacement mapping it will return a copy of
	 * itself, and call replace for all it's child methods.
	 *
	 * @param replacement the replacement mapping
	 * @return the replaced object or a copy of the current object
	 */
	public abstract BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement);

	static class BaseSemanticIterator implements Iterator<BaseSemantic> {

		private final Deque<BaseSemantic> stack;

		public BaseSemanticIterator(BaseSemantic start) {
			stack = new ArrayDeque<>();
			createStack(start);
		}

		private void createStack(BaseSemantic start) {
			stack.offer(start);
			start.getChildren().forEach(this::createStack);
		}

		@Override
		public boolean hasNext() {
			return !stack.isEmpty();
		}

		@Override
		public BaseSemantic next() {
			if (!hasNext())
				throw new NoSuchElementException();
			return stack.pop();
		}
	}

	public final InterpreterNode getBasedOn() {
		return basedOn;
	}

	public final int getId() {
		return id;
	}

	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitBaseSemantic(this);
	}

}
