package at.fhooe.se.atgminer.atg;

import java.util.Objects;

/**
 * A terminal class of an attribute grammar. A terminal class is a terminal
 * symbol with a token (e.g. {@code INTEGER}) and an attribute value (e.g.
 * {@code 42}.
 */
public class ATTerminalClass extends ATTerminal {

	private final String attributeValue;

	ATTerminalClass(String token, String attributeValue) {
		super(token);
		this.attributeValue = Objects.requireNonNull(attributeValue);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Note that a terminal class is identified by its token only and the attribute
	 * is not considered for checking equivalence of two terminal class symbols.
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public <T> T accept(ATElementVisitor<T> v) {
		return v.visitTerminalClass(this);
	}

	/**
	 * Returns the attribute value as string.
	 *
	 * @return the attribute value as string
	 */
	public String getAttributeValue() {
		return attributeValue;
	}

	/**
	 * This is work in progress. For a terminal class a strategy is needed to
	 * generate input tokens.
	 *
	 * @return the input string of this terminal class
	 */
	public String generateInputString() {
		return attributeValue;
	}

}
