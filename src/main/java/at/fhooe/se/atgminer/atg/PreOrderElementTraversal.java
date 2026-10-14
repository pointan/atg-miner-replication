package at.fhooe.se.atgminer.atg;

public class PreOrderElementTraversal {

	public <T> void traverse(ATElement element, AbstractATElementVisitor<T> visitor) {
		element.accept(visitor);
		for (ATElement e : element.getElements()) {
			traverse(e, visitor);
		}
	}

}
