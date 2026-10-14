package at.fhooe.se.atgminer.atg;

import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.visitor.ToStringVisitor;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class ATElement implements Iterable<ATElement> {
	private static int nextID;

	private final int id;

	private final List<BaseSemantic> preSemantics;
	private final List<BaseSemantic> postSemantics;

	private boolean wasUsed = false;

    private String semStr;

	ATElement() {
		this.id = nextID++;
		preSemantics = new ArrayList<>();
		postSemantics = new ArrayList<>();
	}

	public List<ATElement> getElements() {
		return Arrays.asList();
	}

	public abstract <T> T accept(ATElementVisitor<T> v);

	public abstract String getKey();

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();

	public int getID() {
		return id;
	}

	public List<BaseSemantic> getPreSemantics() {
		return preSemantics;
	}

	public List<BaseSemantic> getPostSemantics() {
		return postSemantics;
	}

	@Override
	public Iterator<ATElement> iterator() {
		Deque<ATElement> stack = new ArrayDeque<>();
		new Object() {
			void buildStack(ATElement tree) {
				stack.add(tree);
				tree.getElements().forEach(this::buildStack);
			}
		}.buildStack(this);

		return stack.iterator();
	}

	public Stream<ATElement> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	/**
	 * This method is just a helper class, which converts ATNonTerminals into the correct static type, that avoid the need to cast it everytime and makes test more readable.
	 */
	public ATNonTerminal asNonTerminal() {
		throw new IllegalStateException("Cannot convert " + getClass().getSimpleName() + " to ATNonTerminal");
	}

	@Override
	public String toString() {
		return this.accept(ToStringVisitor.INSTANCE);
	}

	public boolean isWasUsed() {
		return wasUsed;
	}

	public void setWasUsed(boolean wasUsed) {
		this.wasUsed = wasUsed;
	}

    public String getSemStr() {
        return semStr;
    }

    public void setSemStr(String semStr) {
        this.semStr = semStr;
    }
}
