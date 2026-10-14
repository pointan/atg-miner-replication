package at.fhooe.se.atgminer.atg;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ATRepetition extends ATElement {

	private final ATElement body;

	ATRepetition() {
		this(null);
	}

	ATRepetition(ATElement body) {
		this.body = body;
	}

	@Override
	public List<ATElement> getElements() {
		List<ATElement> list = new ArrayList<>();
		list.add(body);
		return list;
	}

	public ATElement getBody() {
		return body;
	}

	@Override
	public <T> T accept(ATElementVisitor<T> v) {
		return v.visitRepetition(this);
	}

	@Override
	public String getKey() {
		return "(Repetition-"+getID()+")";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ATRepetition)) return false;
		ATRepetition that = (ATRepetition) o;
		return getBody().equals(that.getBody());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getBody());
	}
}
