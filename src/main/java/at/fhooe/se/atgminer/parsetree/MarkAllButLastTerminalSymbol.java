package at.fhooe.se.atgminer.parsetree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MarkAllButLastTerminalSymbol implements ParseTreeVisitor {

	private final Set<Integer> markedNodeSet;

	public MarkAllButLastTerminalSymbol() {
		this.markedNodeSet = new HashSet<>();
	}

	@Override
	public void visit(ParseTree node) {
		markLeftMostTerminal(node);
	}

	private void markLeftMostTerminal(ParseTree node) {
		List<ParseTree> reverse = new ArrayList<>(node.getChildren());
		Collections.reverse(reverse);
		reverse.forEach((e) -> markLeftMostTerminal(e));

		if (node.isTerminalSymbol()) {
			if (markedNodeSet.contains(node.getStartIndex())) {
				node.setAlive(false);
			} else {
				// a terminal symbol may consist of multiple characters
				for (int i = 0; i < node.getLabel().length(); i++) {
					markedNodeSet.add(node.getStartIndex() + i);
				}
			}
		}
	}

}
