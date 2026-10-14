package at.fhooe.se.atgminer.atg.coco;

import at.fhooe.se.atgminer.atg.ATChoice;
import at.fhooe.se.atgminer.atg.ATElement;
import at.fhooe.se.atgminer.atg.ATGrammar;

/**
 * Provides set operations (union, diff) and construction operations for Coco
 * character sets (section CHARACTERS).
 * <p>
 * Character sets (as well as tokens) are modelled as non-terminal symbols
 * together with grammar rules.
 */
class CharacterSet {

	private final ATGrammar grammar;

	CharacterSet(ATGrammar grammar) {
		this.grammar = grammar;
	}

	ATElement createFromChar(String value) {
		return grammar.createTerminal(value);
	}

	ATElement createFromRange(String fromStr, String toStr) {
		var set = grammar.createChoice();

		for (char c = fromStr.charAt(0); c <= toStr.charAt(0); c++) {
			set.add(grammar.createTerminal(Character.toString(c)));
		}

		return set;
	}

	ATElement createFromString(String values) {
		var set = grammar.createChoice();
		for (char c : values.toCharArray()) {
			set.add(grammar.createTerminal(Character.toString(c)));
		}
		return set;
	}

	ATElement diff(ATElement a, ATElement b) {
		var set = grammar.createChoice();
		a.getElements().forEach(set::add);
		for (var e : b.getElements()) {
			for (var e2 : a.getElements()) {
				if (e.equals(e2)) {
					set.remove(e2);
				}
			}
		}
		return set;
	}

	ATElement union(ATElement a, ATElement b) {
		var set = grammar.createChoice();
		add(set, a);
		add(set, b);
		return set;
	}

	ATElement createAny() {
		return union(union(createFromRange("A", "Z"), createFromRange("a", "z")), createFromRange("0", "9"));
	}

	private void add(ATChoice set, ATElement a) {
		if (a.getElements().isEmpty()) {
			set.add(a);
		} else {
			a.getElements().forEach(set::add);
		}
	}

}
