package at.fhooe.se.atgminer.atg.impl;

import java.util.HashSet;
import java.util.Set;

import at.fhooe.se.atgminer.atg.*;

/**
 * Calculates the set of terminal start symbols of a grammar element.
 */
public class FirstSetVisitor extends AbstractATElementVisitor<Set<ATTerminal>> {

	private final ATGrammar grammar;
	private final Set<ATElement> guard;

	public FirstSetVisitor(ATGrammar g) {
		this.grammar = g;
		this.guard = new HashSet<>();
	}

	@Override
	public Set<ATTerminal> visitGrammar(ATGrammar grammar) {
		return super.visitGrammar(grammar);
	}

	@Override
	public Set<ATTerminal> visitTerminal(ATTerminal symbol) {
		return Set.of(symbol);
	}

	@Override
	public Set<ATTerminal> visitTerminalClass(ATTerminalClass symbol) {
		return Set.of(symbol);
	}

	@Override
	public Set<ATTerminal> visitNonTerminal(ATNonTerminal symbol) {
		if (guard.contains(symbol)) {
			return Set.of();
		}
		guard.add(symbol);

		ATRule r = grammar.findRuleOf(symbol);
		if (r != null) {
			return r.getBody().accept(this);
		} else {
			throw new RuntimeException("Incomplete grammar: Rule for non-terminal " + symbol.getKey() + " not found.");
		}
	}

	@Override
	public Set<ATTerminal> visitSequence(ATSequence element) {
		if (element.getElements().isEmpty()) {
			return Set.of();
		} else {
			Set<ATTerminal> res = new HashSet<>();
			for (ATElement e : element.getElements()) {
				res.addAll(e.accept(this));
				if (!isOptional(e)) {
					break;
				}
			}
			return res;
		}
	}

	@Override
	public Set<ATTerminal> visitOption(ATOption element) {
		var e = element.getElements().get(0);
		return (e != null) ? e.accept(this) : Set.of();
	}

	@Override
	public Set<ATTerminal> visitRepetition(ATRepetition element) {
		var e = element.getElements().get(0);
		return (e != null) ? e.accept(this) : Set.of();
	}

	@Override
	public Set<ATTerminal> visitChoice(ATChoice element) {
		var first = new HashSet<ATTerminal>();
		for (var e : element.getElements()) {
			first.addAll(e.accept(this));
		}
		return first;
	}

	private boolean isOptional(ATElement e) {
		return e.accept(new ThrowingATElementVisitor<>() {
			@Override
			public Boolean visitTerminal(ATTerminal symbol) {
				return false;
			}

			@Override
			public Boolean visitTerminalClass(ATTerminalClass symbol) {
				return false;
			}

			@Override
			public Boolean visitNonTerminal(ATNonTerminal symbol) {
				return false;
			}

			@Override
			public Boolean visitSequence(ATSequence element) {
				return false;
			}

			@Override
			public Boolean visitOption(ATOption element) {
				return true;
			}

			@Override
			public Boolean visitRepetition(ATRepetition element) {
				return true;
			}

			@Override
			public Boolean visitChoice(ATChoice element) {
				return false;
			}

			@Override
			public Boolean visitTerminalGroup(ATTerminalGroup group) {
				return false;
			}
		});
	}

}
