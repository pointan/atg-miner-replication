package at.fhooe.se.atgminer.concept.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import at.fhooe.se.atgminer.atg.ATElement;
import at.fhooe.se.atgminer.atg.ATGrammar;
import at.fhooe.se.atgminer.atg.ATNonTerminal;

/**
 * A symbol subscript is used to provide unique keys for nonterminal symbols
 * within a grammar. For instance, the symbol {@code Term} appears three times
 * in the following grammar rule.
 * 
 * <pre>
 * Expr = Term [ '+' Term | '-' Term ].
 * </pre>
 * 
 * A subscript is used to distinguish between occurrences of the like symbol.
 * 
 * <pre>
 * Expr = Term<sub>1</sub> [ '+' Term<sub>2</sub> | '-' Term<sub>3</sub> ].
 * </pre>
 * 
 * Assuming an attribute {@code v}, the attribute is accessed by
 * 
 * <pre>
 * v(Term<sub>2</sub>)
 * </pre>
 * 
 * Maybe in future the subscripts are modelled in the grammar symbol directly.
 */
public class SymbolSubscript {
	class Entry {
		final ATNonTerminal symbol;
		final int subscript;

		public Entry(ATNonTerminal symbol, int subscript) {
			this.symbol = symbol;
			this.subscript = subscript;
		}

		@Override
		public String toString() {
			return symbol.getKey() + "" + subscript;
		}
	}

	private final Map<Integer, Entry> subscripts;

	public SymbolSubscript() {
		this.subscripts = new HashMap<>();
	}

	@Override
	public String toString() {
		return this.subscripts.values().toString();
	}

	public int getSubscriptOf(ATNonTerminal symbol) {
		if (subscripts.containsKey(symbol.getID())) {
			return subscripts.get(symbol.getID()).subscript;
		} else {
			return 0;
		}
	}

	public SymbolSubscript fillSubscripts(ATGrammar grammar) {
		for (var rule : grammar.getRuleList()) {
			Map<Integer, Entry> ruleSubscripts = new HashMap<>();
			Map<String, Integer> keyCnt = new HashMap<>();
			addElement(rule.getKey(), keyCnt, ruleSubscripts);
			traverseElement(rule.getBody(), keyCnt, ruleSubscripts);

			// Remove subscript `1` if a nonterminal appears only once.
			for (var key : keyCnt.keySet()) {
				if (keyCnt.get(key) == 1) {
					for (var e : new ArrayList<>(ruleSubscripts.values())) {
						if (key == e.symbol.getKey()) {
							ruleSubscripts.remove(e.symbol.getID());
						}
					}
				}
			}

			this.subscripts.putAll(ruleSubscripts);
		}
		return this;
	}

	private void traverseElement(ATElement element, Map<String, Integer> keyCnt, Map<Integer, Entry> ruleSubscripts) {
		if (element instanceof ATNonTerminal) {
			addElement((ATNonTerminal) element, keyCnt, ruleSubscripts);
		}
		if (element != null) {
			for (var e : element.getElements()) {
				traverseElement(e, keyCnt, ruleSubscripts);
			}
		}
	}

	private void addElement(ATNonTerminal element, Map<String, Integer> keyCnt, Map<Integer, Entry> ruleSubscripts) {
		int index = 1;
		if (keyCnt.containsKey(element.getKey())) {
			index = keyCnt.get(element.getKey()) + 1;
			keyCnt.put(element.getKey(), index);
		} else {
			keyCnt.put(element.getKey(), index);
		}
		ruleSubscripts.put(element.getID(), new Entry(element, index));
	}

}
