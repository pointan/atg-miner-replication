package at.fhooe.se.atgminer.parsetree;

public class MarkParentVisitor implements ParseTreeVisitor {

	public MarkParentVisitor() {
	}

	@Override
	public void visit(ParseTree node) {
		markParentNodes(node);
	}

	private boolean markParentNodes(ParseTree node) {

		if (node.getChildren().isEmpty()) {
			return node.isAlive() | node.containsSemantics();
		}

		boolean alive = false;

		for (ParseTree e : node.getChildren()) {
			alive |= markParentNodes(e);
		}
		alive |= node.containsSemantics();
		node.setAlive(alive);
		return alive;
	}

}
