package at.fhooe.se.atgminer.semantic.enrichment;

import at.fhooe.se.atgminer.atg.ATElement;
import at.fhooe.se.atgminer.atg.ATGrammar;
import at.fhooe.se.atgminer.atg.ATNonTerminal;
import at.fhooe.se.atgminer.atg.ATRule;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SymbolSubscript {
	public int getSubscriptOf(ATNonTerminal symbol) {
		return subscripts.getOrDefault(symbol.getID(), 0);
	}

	private final Map<Integer, Integer> subscripts;

	public SymbolSubscript() {
		subscripts = new HashMap<>();
	}

	public void fillSubscripts(ATGrammar grammar) {
		for (ATRule atRule : grammar.getRuleList()) {

			Map<String, List<ATNonTerminal>> collect =
					atRule.getBody().stream()
					.filter(ATNonTerminal.class::isInstance)
					.map(ATNonTerminal.class::cast)
					.collect(Collectors.toMap(ATElement::getKey, List::of, (x, y) -> Stream.of(x, y).flatMap(Collection::stream).collect(Collectors.toList())));

			for (List<ATNonTerminal> value : collect.values()) {
				int size = value.size();
				if (size > 1) {
					for (int i = 0; i < size; i++) {
						ATNonTerminal elem = value.get(i);
						subscripts.put(elem.getID(), i + 1);
					}
				}
			}
		}
	}
}
