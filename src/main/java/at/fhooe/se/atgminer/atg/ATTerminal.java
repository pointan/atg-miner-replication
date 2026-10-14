package at.fhooe.se.atgminer.atg;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ATTerminal extends ATElement implements Comparable<ATTerminal> {

	public final static String EPSILON_KEY = "ɛ";

	private final String token;

	public ATTerminal(String token) {
		this.token = Objects.requireNonNull(token);
	}

	@Override
	public int hashCode() {
		return token.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ATTerminal other = (ATTerminal) obj;

		return token.equals(other.token);
	}

	@Override
	public <T> T accept(ATElementVisitor<T> v) {
		return v.visitTerminal(this);
	}

	@Override
	public String getKey() {
		return token;
	}

	public String getToken() {
		return token;
	}

	@Override
	public int compareTo(ATTerminal o) {
		return token.compareTo(o.token);
	}

	/**
	 * Creates a new epsilon symbol with token/key {@code ɛ}.
	 *
	 * @return a new epsilon symbol
	 */
	public static ATTerminal createEpsilon() {
		return new ATTerminal(EPSILON_KEY);
	}

	/**
	 * Answers whether the given symbol is an epsilon symbol.
	 *
	 * @param symbol the symbol to test
	 * @return {@code true} if the given symbol is an epsilon symbol; {@code false}
	 *         otherwise
	 */
	public static boolean isEpsilon(ATElement symbol) {
		return symbol instanceof ATTerminal && EPSILON_KEY.equals(symbol.getKey());
	}

	public static List<ATTerminal> listFromString(String inputString) {
		var list = new ArrayList<ATTerminal>();
		for (int i = 0; i < inputString.length(); i++) {
			list.add(new ATTerminal("" + inputString.charAt(i)));
		}
		return list;
	}
}
