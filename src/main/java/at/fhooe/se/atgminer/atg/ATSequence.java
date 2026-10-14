package at.fhooe.se.atgminer.atg;

import java.util.*;

public class ATSequence extends ATElement {

	private final List<ATElement> seq;

	ATSequence() {
		this.seq = new ArrayList<>();
	}

	public ATSequence(ATElement[] seq) {
		this.seq = Arrays.asList(seq);
	}

	public void add(ATElement e) {
		this.seq.add(e);
	}

	public List<ATElement> getElements() {
		return seq;
	}

	public int size() {
		return seq.size();
	}
	public ATElement get(int index) {
		return seq.get(index);
	}

	@Override
	public <T> T accept(ATElementVisitor<T> v) {
		return v.visitSequence(this);
	}

	@Override
	public String getKey() {
		return "..";
	}

	public List<ATElement> getSeq() {
		return seq;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ATSequence)) return false;
		ATSequence sequence = (ATSequence) o;
		return getSeq().equals(sequence.getSeq());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getSeq());
	}

}
