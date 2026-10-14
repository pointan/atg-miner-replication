package at.fhooe.se.atgminer.concept.v2;

/**
 * Utilities for naming grammar symbols and symbol attributes.
 */
public final class AttributeName {

	private AttributeName() {
	}

	/**
	 * Returns the unique attribute name of a non-terminal symbol.
	 *
	 * @param symbolName the name of the non-terminal symbol
	 * @return the unique attribute name of the non-terminal symbol
	 */
	public static String formalAttributeNameOfSymbol(String symbolName) {
		if (symbolName.length() >= 1) {
			char key = Character.toUpperCase(symbolName.charAt(0));
			return "" + key;
		} else {
			return "?";
		}
	}

	public static String replaceSubscripts(String str) {
		str = str.replace("0", "\u2080");
		str = str.replace("1", "\u2081");
		str = str.replace("2", "\u2082");
		str = str.replace("3", "\u2083");
		str = str.replace("4", "\u2084");
		str = str.replace("5", "\u2085");
		str = str.replace("6", "\u2086");
		str = str.replace("7", "\u2087");
		str = str.replace("8", "\u2088");
		str = str.replace("9", "\u2089");
		return str;
	}

	public static String removeSubscripts(String str) {
		return str
				.replace("\u2081", "")
				.replace("\u2082", "")
				.replace("\u2083", "")
				.replace("\u2084", "")
				.replace("\u2085", "")
				.replace("\u2086", "")
				.replace("\u2087", "")
				.replace("\u2088", "")
				.replace("\u2089", "")
				.replace("_1_", "")
				.replace("_2_", "")
				.replace("_3_", "")
				.replace("_4_", "")
				.replace("_5_", "")
				.replace("_6_", "")
				.replace("_7_", "")
				.replace("_8_", "")
				.replace("_9_", "")
				;
	}

	public static String subscriptString(int subscript) {
		switch (subscript) {
		case 0:
			return "";
		case 1:
			return "\u2081";
		case 2:
			return "\u2082";
		case 3:
			return "\u2083";
		case 4:
			return "\u2084";
		case 5:
			return "\u2085";
		case 6:
			return "\u2086";
		case 7:
			return "\u2087";
		case 8:
			return "\u2088";
		case 9:
			return "\u2089";
		default:
			return Integer.toString(subscript);
		}
	}

}
