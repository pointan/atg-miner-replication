package at.fhooe.se.atgminer.parsetree;

public class RemoveMarkedNodesVisitor implements ParseTreeVisitor {

	public RemoveMarkedNodesVisitor() {
	}

	@Override
	public void visit(ParseTree node) {
		removeMarkedChildren(node);
	}

	private void removeMarkedChildren(ParseTree node) {
		node.getChildren().forEach((e) -> removeMarkedChildren(e));
		node.getChildren().removeIf((e) -> !e.isAlive());
	}

}
