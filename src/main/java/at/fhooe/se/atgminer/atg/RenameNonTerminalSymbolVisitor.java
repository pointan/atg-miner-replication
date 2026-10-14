package at.fhooe.se.atgminer.atg;

import java.util.HashMap;
import java.util.Map;

public class RenameNonTerminalSymbolVisitor extends GrammarDeepCopy {

	private final Map<String, String> keyMap;
	private final Map<String, ATNonTerminal> symbolMap;

	public RenameNonTerminalSymbolVisitor(Map<String, String> keyMap) {
		this.keyMap = keyMap;
		this.symbolMap = new HashMap<>();
	}

	public ATGrammar apply(ATGrammar grammar) {
		symbolMap.clear();
		return super.apply(grammar);
	}

	@Override
	public ATElement visitNonTerminal(ATNonTerminal symbol) {
		String oldKey = symbol.getKey();
		if (keyMap.containsKey(oldKey)) {
			String newKey = keyMap.get(oldKey);
			if (!symbolMap.containsKey(newKey)) {
				symbolMap.put(newKey, this.getCopy().createNonTerminal(newKey));
			}
			return symbolMap.get(newKey);
		} else {
			return symbol;
		}

	}

}
