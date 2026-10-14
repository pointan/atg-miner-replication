package at.fhooe.se.atgminer.parsetree;

public class MarkNonTerminalLeafVisitor implements ParseTreeVisitor {

	public MarkNonTerminalLeafVisitor() {
	}

	@Override
	public void visit(ParseTree node) {
		markLastTerminalSymbol(node);
	}

	private void markLastTerminalSymbol(ParseTree node) {
		if (!node.isTerminalSymbol() && node.getChildren().isEmpty()) {
			node.setAlive(false);
		} else {
			node.getChildren().forEach((e) -> markLastTerminalSymbol(e));
		}
	}

}
