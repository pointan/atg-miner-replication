package at.fhooe.se.atgminer.atg.impl;

import at.fhooe.se.atgminer.atg.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Allows parsing a grammar</p>
 *
 * <pre>
 * Grammar (G):
 * G = 'Grammar' '(' NT ')' ':' { Rule } .
 * Rule = NT '=' RuleBody .
 * RuleBody = { GrammarElem } [{'|' RuleBody}].
 * GrammarElem = NT | Repetition | Option | Terminal | Grouping .
 * Grouping = '(' RuleBody ')' .
 * Repetition = '{' RuleBody '}' .
 * Optional = '[' RuleBody ']' .
 * Option = '|' RuleBody .
 * NT = 'A'..'Z' { 'a'..'z' } .
 * Terminal = "'" { CHAR } "'" .
 * </pre>
 */
public class GrammarParser implements GrammarReader {

	@SuppressWarnings("java:S115")
	enum Sy {
		IdentSy,
		TextSy,
		GrammarSy,
		OpenSquareBracketSy,
		CloseSquareBracketSy,
		OpenParenthesesSy,
		CloseParenthesesSy,
		OpenCurlyBracketSy,
		CloseCurlyBracketSy,
		QuotationMarkStartSy,
		QuotationMarkEndSy,
		VerticalBarSy,
		ColonSy,
		EqualSy,
		EndSy,
		DotSy
	}

	private char currentChar;
	private Sy currentSy;
	private String input;
	private int idx;
	private ATGrammar grammar;
	private String ident;

	private void nextChar() {
		if (idx >= input.length()) {
			currentChar = 0;
			return;
		}
		currentChar = input.charAt(idx++);
	}

	private void nextSy() {
		while (Character.isWhitespace(currentChar)) {
			nextChar();
			if (currentChar == 0) {
				currentSy = Sy.EndSy;
				return;
			}
		}

		if (currentSy == Sy.QuotationMarkStartSy) {
			StringBuilder terminalBuilder = new StringBuilder();
			while (currentChar != '\'') {
				terminalBuilder.append(currentChar);
				nextChar();
			}
			ident = terminalBuilder.toString();
			currentSy = Sy.TextSy;
			return;
		}

		switch (currentChar) {
			case '\'':
				currentSy = currentSy == Sy.TextSy ? Sy.QuotationMarkEndSy : Sy.QuotationMarkStartSy;
				nextChar();
				break;
			case '(':
				currentSy = Sy.OpenParenthesesSy;
				nextChar();
				break;
			case ')':
				currentSy = Sy.CloseParenthesesSy;
				nextChar();
				break;
			case '[':
				currentSy = Sy.OpenSquareBracketSy;
				nextChar();
				break;
			case ']':
				currentSy = Sy.CloseSquareBracketSy;
				nextChar();
				break;
			case '{':
				currentSy = Sy.OpenCurlyBracketSy;
				nextChar();
				break;
			case '}':
				currentSy = Sy.CloseCurlyBracketSy;
				nextChar();
				break;
			case ':':
				currentSy = Sy.ColonSy;
				nextChar();
				break;
			case '=':
				currentSy = Sy.EqualSy;
				nextChar();
				break;
			case '.':
				currentSy = Sy.DotSy;
				nextChar();
				break;
			case '|':
				currentSy = Sy.VerticalBarSy;
				nextChar();
				break;
			case 0:
				currentSy = Sy.EndSy;
				nextChar();
				break;
			default:
				StringBuilder identBuilder = new StringBuilder();
				while ((currentChar >= 'A' && currentChar <= 'Z') || (currentChar >= 'a' && currentChar <= 'z') || currentChar == '_' || (currentChar >= '0' && currentChar <= '9') | currentChar == ':') {
					identBuilder.append(currentChar);
					nextChar();
					if (currentChar == 0) {
						currentSy = Sy.EndSy;
						return;
					}
				}
				String identStr = identBuilder.toString();
				if (identStr.equals("Grammar")) {
					currentSy = Sy.GrammarSy;
				} else {
					currentSy = Sy.IdentSy;
					ident = identStr;
				}
				break;
		}
	}

	public ATGrammar readGrammar(String input) {
		this.input = input;
		idx = 0;
		nextChar();
		nextSy();
		return grammar();
	}

	@Override
	public ATGrammar readGrammar(InputStream input) {
		this.input = new BufferedReader(new InputStreamReader(input))
				.lines().collect(Collectors.joining("\n"));
		idx = 0;
		nextChar();
		nextSy();
		return grammar();
	}

	private ATGrammar grammar() {
		grammar = new ATGrammar();
		if (currentSy != Sy.GrammarSy) {
			throw new ParserError(idx, "Expected Grammar element");
		}
		nextSy();

		if (currentSy != Sy.OpenParenthesesSy) {
			throw new ParserError(idx, "Expected ( element");
		}
		nextSy();

		if (currentSy != Sy.IdentSy) {
			throw new ParserError(idx, "Expected ident element");
		}
		grammar.setStartSymbol(grammar.createNonTerminal(ident));
		nextSy();

		if (currentSy != Sy.CloseParenthesesSy) {
			throw new ParserError(idx, "Expected ) element");
		}
		nextSy();

		if (currentSy != Sy.ColonSy) {
			throw new ParserError(idx, "Expected : element");
		}
		nextSy();

		while (currentSy != Sy.EndSy) {
			grammar.addRule(rule());
		}

		return grammar;
	}

	private ATRule rule() {
		if (currentSy != Sy.IdentSy) {
			throw new ParserError(idx, "Expected ident element");
		}
		String ruleNtName = ident;
		nextSy();

		if (currentSy != Sy.EqualSy) {
			throw new ParserError(idx, "Expected = element");
		}
		nextSy();

		ATElement body = ruleBody();

		if (currentSy != Sy.DotSy) {
			throw new ParserError(idx, "Expected . element");
		}
		nextSy();

		return grammar.createRule(grammar.createNonTerminal(ruleNtName), body);
	}

	private ATElement ruleBody() {
		List<ATElement> choices = new ArrayList<>();

		boolean should_break = false;

		while (currentSy != Sy.DotSy && !should_break) {
			if (!choices.isEmpty()) {
				nextSy(); // skip The VerticalBarSy
			}

			ATSequence sequence = grammar.createSequence();
			while (currentSy != Sy.DotSy && currentSy != Sy.VerticalBarSy && !should_break) {
				if (currentSy == Sy.OpenCurlyBracketSy) {
					sequence.add(repetition());
				} else if (currentSy == Sy.OpenSquareBracketSy) {
					sequence.add(option());
				} else if (currentSy == Sy.IdentSy) {
					sequence.add(nt());
				} else if (currentSy == Sy.QuotationMarkStartSy) {
					sequence.add(terminal());
				} else if (currentSy == Sy.OpenParenthesesSy) {
					sequence.add(grouping());
				} else if (currentSy == Sy.CloseCurlyBracketSy || currentSy == Sy.CloseSquareBracketSy || currentSy == Sy.CloseParenthesesSy) {
					should_break = true;
				} else {
					throw new ParserError(idx, "No valid rule.");
				}
			}

			if (sequence.getElements().isEmpty()) {
				throw new ParserError(idx, "Rule needs at least a single body element");
			}

			if (sequence.getElements().size() == 1) {
				choices.add(sequence.getElements().get(0));
			} else {
				choices.add(sequence);
			}
		}

		if (choices.isEmpty()) {
			throw new ParserError(idx, "Rule needs at least a single body element");
		}

		if (choices.size() == 1) {
			return choices.get(0);
		}

		return grammar.createChoice(choices.toArray(ATElement[]::new));
	}

	private ATSequence grouping() {
		if (currentSy != Sy.OpenParenthesesSy) {
			throw new ParserError(idx, "Expected ( element");
		}
		nextSy();

		ATSequence seq = grammar.createSequence(ruleBody());

		if (currentSy != Sy.CloseParenthesesSy) {
			throw new ParserError(idx, "Expected ) element");
		}
		nextSy();

		return seq;
	}

	private ATNonTerminal nt() {
		if (currentSy != Sy.IdentSy) {
			throw new ParserError(idx, "Expected ident element");
		}
		String ntName = ident;
		nextSy();
		return grammar.createNonTerminal(ntName);
	}

	private ATRepetition repetition() {
		if (currentSy != Sy.OpenCurlyBracketSy) {
			throw new ParserError(idx, "Expected { element");
		}
		nextSy();

		ATRepetition rep = grammar.createRepetition(ruleBody());

		if (currentSy != Sy.CloseCurlyBracketSy) {
			throw new ParserError(idx, "Expected } element");
		}
		nextSy();

		return rep;
	}

	private ATOption option() {
		if (currentSy != Sy.OpenSquareBracketSy) {
			throw new ParserError(idx, "Expected [ element");
		}
		nextSy();

		ATOption op = grammar.createOption(ruleBody());

		if (currentSy != Sy.CloseSquareBracketSy) {
			throw new ParserError(idx, "Expected ] element");
		}
		nextSy();

		return op;
	}

	private ATTerminal terminal() {
		if (currentSy != Sy.QuotationMarkStartSy) {
			throw new ParserError(idx, "Expected ' element");
		}
		nextSy();

		if (currentSy != Sy.TextSy) {
			throw new ParserError(idx, "Expected Text element");
		}
		ATTerminal terminal = grammar.createTerminal(ident);
		nextSy();

		if (currentSy != Sy.QuotationMarkEndSy) {
			throw new ParserError(idx, "Expected ' element");
		}
		nextSy();

		return terminal;
	}


	public static class ParserError extends RuntimeException {
		public ParserError(int pos, String msg) {
			super(msg + " - failed parsing at " + pos);
		}
	}


}
