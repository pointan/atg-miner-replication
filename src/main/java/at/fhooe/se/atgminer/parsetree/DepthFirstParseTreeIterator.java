package at.fhooe.se.atgminer.parsetree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Iterator implementation for a Parse Tree</p>
 */
public class DepthFirstParseTreeIterator implements Iterator<ParseTree> {

	private final Deque<ParseTree> stack;

	public DepthFirstParseTreeIterator(ParseTree pt) {
		stack = new ArrayDeque<>();
		this.buildStack(pt);
	}

	private void buildStack(ParseTree pt) {
		stack.addLast(pt);
		pt.getChildren().forEach(this::buildStack);
	}

	@Override
	public boolean hasNext() {
		return !stack.isEmpty();
	}

	@Override
	public ParseTree next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return stack.pop();
	}
}
