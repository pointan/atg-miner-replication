package at.fhooe.se.atgminer.atg;

import java.util.function.UnaryOperator;

public class GrammarDeepCopy extends ThrowingATElementVisitor<ATElement> implements UnaryOperator<ATGrammar> {

	private ATGrammar originalGrammar;
	private ATGrammar copyGrammar;

	public GrammarDeepCopy() {
		copyGrammar = new ATGrammar();
	}
	public ATGrammar apply(ATGrammar grammar) {
		originalGrammar = grammar;
		copyGrammar = new ATGrammar();
		copyGrammar.setStartSymbol(grammar.getStartSymbol());
		for (ATRule e : grammar.getRuleList()) {
			ATRule copy = copyGrammar.createRule((ATNonTerminal) e.getKey().accept(this), e.getBody().accept(this));
			copyGrammar.addRule(copy);
		}
		return copyGrammar;
	}

	@Override
	public ATSequence visitSequence(ATSequence element) {
		var copy = copyGrammar.createSequence();
		for (ATElement e : element.getElements()) {
			copy.add(e.accept(this));
		}
		return copy;
	}

	@Override
	public ATElement visitRepetition(ATRepetition element) {
		return copyGrammar.createRepetition(element.getBody().accept(this));
	}

	@Override
	public ATElement visitOption(ATOption element) {
		return copyGrammar.createOption(element.getBody().accept(this));
	}

	@Override
	public ATElement visitChoice(ATChoice element) {
		var copy = copyGrammar.createChoice();
		for (ATElement e : element.getElements()) {
			copy.add(e.accept(this));
		}
		return copy;
	}

	@Override
	public ATElement visitTerminal(ATTerminal symbol) {
		return copyGrammar.createTerminal(symbol.getToken());
	}

	@Override
	public ATElement visitTerminalClass(ATTerminalClass symbol) {
		return copyGrammar.createTerminalClass(symbol.getToken(), symbol.getAttributeValue());
	}

	@Override
	public ATElement visitNonTerminal(ATNonTerminal symbol) {
		return copyGrammar.createNonTerminal(symbol.getKey());
	}

	@Override
	public ATElement visitTerminalGroup(ATTerminalGroup group) {
		// these are singletons
		return group;
	}

	protected ATGrammar getCopy() {
		return copyGrammar;
	}

	protected ATGrammar getOriginal() {
		return originalGrammar;
	}

}
