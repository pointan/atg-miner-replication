package at.fhooe.se.atgminer.atg;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ATOption extends ATElement {

	private final ATElement body;

	public ATOption(ATElement body) {
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
		return v.visitOption(this);
	}

	@Override
	public String getKey() {
		return "(Option-"+getID()+")";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ATOption)) return false;
		ATOption atOption = (ATOption) o;
		return getBody().equals(atOption.getBody());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getBody());
	}
}
