package at.fhooe.se.atgminer.atg;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import at.fhooe.se.atgminer.atg.semantics.InOutAttribute;

public class ATNonTerminal extends ATElement implements Comparable<ATNonTerminal> {

	public static final ATNonTerminal EMPTY_ELEMENT = new ATNonTerminal("ε");

	private final String key;

	private final Set<InOutAttribute> inheritedAttributes;
	private final Set<InOutAttribute> synthesizedAttributes;

	public ATNonTerminal(String key) {
		super();
		this.key = key;
		this.inheritedAttributes = new HashSet<>();
		this.synthesizedAttributes = new HashSet<>();
	}

	@Override
	public <T> T accept(ATElementVisitor<T> v) {
		return v.visitNonTerminal(this);
	}

	@Override
	public String getKey() {
		return key;
	}

	public Set<InOutAttribute> getInheritedAttributes() {
		return inheritedAttributes;
	}

	public Set<InOutAttribute> getSynthesizedAttributes() {
		return synthesizedAttributes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ATNonTerminal))
			return false;
		ATNonTerminal that = (ATNonTerminal) o;
		return getKey().equals(that.getKey());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getKey());
	}

	@Override
	public int compareTo(ATNonTerminal o) {
		return key.compareTo(o.key);
	}

	public boolean hasAttributes() {
		var empty = inheritedAttributes.isEmpty() && synthesizedAttributes.isEmpty();
		return !empty;
	}

	@Override
	public ATNonTerminal asNonTerminal() {
		return this;
	}
}
