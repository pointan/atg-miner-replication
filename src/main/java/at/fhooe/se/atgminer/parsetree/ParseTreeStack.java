package at.fhooe.se.atgminer.parsetree;

import java.util.*;
import java.util.logging.Logger;

public class ParseTreeStack {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	class StackEntry {
		ParseTree node;
		ParseTreeType type;

		StackEntry(ParseTree node, ParseTreeType type) {
			this.node = node;
			this.type = type;
		}

		@Override
		public String toString() {
			return node.getLabel();
		}

		public ParseTree getNode() {
			return node;
		}
	}

	private final Deque<StackEntry> nodeStack;

	public ParseTreeStack() {
		this.nodeStack = new ArrayDeque<StackEntry>();
	}

	@Override
	public String toString() {
		return nodeStack.toString();
	}

	public void push(ParseTree node) {
		push(node, ParseTreeType.OTHER);
	}

	public void push(ParseTree node, ParseTreeType type) {
		nodeStack.push(new StackEntry(node, type));
	}

	public ParseTree peek() {
		return nodeStack.peek().node;
	}

	public ParseTreeType peekType() {
		return nodeStack.peek().type;
	}

	
	public ParseTree pop() {
		if (!nodeStack.isEmpty()) {
			return nodeStack.pop().node;
		} else {
			LOGGER.warning("Empty node stack");
			return null;
		}
	}

	public boolean isEmpty() {
		return nodeStack.isEmpty();
	}

	public void removeFirstUntil(ParseTreeType type) {
		List<StackEntry> removeList = new ArrayList<>();
		for (StackEntry e : nodeStack) {
			if (e.type == type) {
				break;
			}
			removeList.add(e);
		}
		nodeStack.removeAll(removeList);
	}

	public void removeFirstInclusive(ParseTreeType type) {
		List<StackEntry> removeList = new ArrayList<>();
		for (StackEntry e : nodeStack) {
			removeList.add(e);
			if (e.type == type) {
				break;
			}
		}
		nodeStack.removeAll(removeList);
	}

	public Optional<ParseTree> findFirstOfType(ParseTreeType type) {
		return nodeStack.stream().filter(ns -> ns.type.equals(type)).findFirst().map(StackEntry::getNode);
	}


}
