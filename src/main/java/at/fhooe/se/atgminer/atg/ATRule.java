package at.fhooe.se.atgminer.atg;

import at.fhooe.se.atgminer.atg.visitor.ToStringVisitor;

import java.util.Objects;

public class ATRule {

    private String semString;
	private final ATNonTerminal key;
	private final ATElement body;

	ATRule(ATNonTerminal key, ATElement body) {
		this.key = key;
		this.body = body;
	}

    ATRule(ATNonTerminal key, ATElement body, String semString) {
        this.key = key;
        this.body = body;
        this.semString = semString;
    }

	@Override
	public String toString() {
		return this.accept(ToStringVisitor.INSTANCE);
	}

	public ATElement getBody() {
		return body;
	}

	public ATNonTerminal getKey() {
		return key;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ATRule)) return false;
		ATRule atRule = (ATRule) o;
		return Objects.equals(getKey(), atRule.getKey()) && Objects.equals(getBody(), atRule.getBody());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getKey(), getBody());
	}

	public <T> T accept(ATElementVisitor<T> v) {
		return v.visitRule(this);
	}

    public String getSemString() {
        return semString;
    }
}
