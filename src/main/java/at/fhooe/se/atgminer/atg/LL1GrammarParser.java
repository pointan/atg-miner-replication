package at.fhooe.se.atgminer.atg;

import at.fhooe.se.atgminer.semantic.enrichment.SymbolSubscript;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class LL1GrammarParser {

	private final ATGrammar grammar;
	private final boolean fullTree;

	private final SymbolSubscript symbolSubscript;

	public LL1GrammarParser(ATGrammar grammar) {
		this(grammar, false);
	}

	public LL1GrammarParser(ATGrammar grammar, boolean fullTreeButSequences) {
		this.grammar = grammar;
		this.fullTree = fullTreeButSequences;
		symbolSubscript = new SymbolSubscript();
		symbolSubscript.fillSubscripts(grammar);
	}

	private List<ATTerminal> tryFindBestMatchingTerminals(String input) {
		List<ATTerminal> existingGrammarTerminals = grammar.getTerminalList();
		List<ATTerminal> terminals = new ArrayList<>();
		int start = 0, end = 1;
		while(start < input.length()) {
			while(true) {
				if (end > input.length()) {
					end--;
					break;
				}
				String currentSubString = input.substring(start, end);
				if (existingGrammarTerminals.stream().anyMatch(t -> t.getToken().startsWith(currentSubString))) {
					end++;
				} else {
					end--;
					break;
				}
			}
			// if start and end are both the same, no terminal has been found, that starts with that character --> this terminal might not exist in the grammar
			if (start == end) {
				return null;
			}
			String longestMatchingTermString = input.substring(start, end);

			Optional<ATTerminal> first = existingGrammarTerminals.stream().filter(x -> x.getToken().equals(longestMatchingTermString)).findFirst();
			while(first.isEmpty() && start != end) {
				end--;
				String longestMatchingTermStringInner = input.substring(start, end);
				first = existingGrammarTerminals.stream().filter(x -> x.getToken().equals(longestMatchingTermStringInner)).findFirst();
			}
			if (first.isPresent()) {
				terminals.add(first.get());
			} else {
				throw new IllegalStateException("At least one character in the input cannot be matched to a terminal symbol. Start position: " + start + " in input: " + input);
			}
			start = end;
			end++;
		}
		return terminals;
	}

	public ATSyntaxTree parse(String input) {
		List<ATTerminal> atTerminals = tryFindBestMatchingTerminals(input);
		if (atTerminals == null) {
			throw parseError("Could not find any terminals for " + input);
		}
		return parse(atTerminals);
	}

	public ATSyntaxTree parse(List<ATTerminal> input) {
		ATRule startRule = findRuleOf(grammar.getStartSymbol());
		ATSyntaxTree root = new ATSyntaxTree(grammar.getStartSymbol(), 0);
		List<ATSyntaxTree> children = parse(startRule.getBody(), input, new AtomicInteger(0));
		root.addChildren(children);
		if (!input.isEmpty()) {
			throw new IllegalStateException("Not every element of the input has been processed!");
		}
		return root;
	}

	private List<ATSyntaxTree> parse(ATElement body, List<ATTerminal> input, AtomicInteger inputIndex) {
		ATTerminal laTmp = null;
		if (!input.isEmpty()) {
			laTmp = input.get(0);
		} else {
			laTmp = ATTerminal.createEpsilon();
		}
		final ATTerminal la = laTmp;

		AbstractATElementVisitor<List<ATSyntaxTree>> v = new AbstractATElementVisitor<>() {
			@Override
			public List<ATSyntaxTree> visitTerminal(ATTerminal symbol) {
				if (symbol.equals(la)) {
					input.remove(0);
					List<ATSyntaxTree> atSyntaxTrees = ATSyntaxTree.listOf(inputIndex.get(), symbol);
					inputIndex.set(inputIndex.get() + symbol.getToken().length());
					return atSyntaxTrees;
				} else {
					throw parseError("Expected " + symbol + " but found " + la);
				}
			}

			@Override
			public List<ATSyntaxTree> visitTerminalClass(ATTerminalClass symbol) {
				if (symbol.getKey().equals(la.getKey())) {
					input.remove(0);
					List<ATSyntaxTree> atSyntaxTrees = ATSyntaxTree.listOf(inputIndex.get(), symbol);
					inputIndex.set(inputIndex.get() + symbol.getToken().length());
					return atSyntaxTrees;
				} else {
					throw parseError("Expected " + symbol + " but found " + la);
				}
			}

			@Override
			public List<ATSyntaxTree> visitNonTerminal(ATNonTerminal symbol) {
				ATRule r = findRuleOf(symbol);
				ATSyntaxTree root = ATSyntaxTree.treeOf(symbol, inputIndex.get());
				root.addChildren(parse(r.getBody(), input, inputIndex));
				root.setSymbolIndex(symbolSubscript.getSubscriptOf(symbol));
				return ATSyntaxTree.listOf(root);
			}

			@Override
			public List<ATSyntaxTree> visitSequence(ATSequence element) {
				List<ATSyntaxTree> list = ATSyntaxTree.createList();
				for (ATElement e : element.getSeq()) {
					List<ATSyntaxTree> children = parse(e, input, inputIndex);
					list.addAll(children);
				}
				return list;
			}

			@Override
			public List<ATSyntaxTree> visitRepetition(ATRepetition element) {
				List<ATSyntaxTree> list = ATSyntaxTree.createList();
				var tmp = la;
				while (!input.isEmpty() && firstSet(element).contains(tmp)) {
					List<ATSyntaxTree> children = parse(element.getElements().get(0), input, inputIndex);
					list.addAll(children);
					tmp = input.isEmpty() ? null : input.get(0);
				}

				if (fullTree) {
					ATSyntaxTree root = ATSyntaxTree.treeOf(element, inputIndex.get());
					root.addChildren(list);
					return ATSyntaxTree.listOf(root);
				} else {
					return list;
				}
			}

			@Override
			public List<ATSyntaxTree> visitChoice(ATChoice element) {
					int cnt = 0;
					ATElement sel = null;
					for (ATElement e : element.getElements()) {
						Set<ATTerminal> first = firstSet(e);
						if (first.contains(la)) {
							if (sel == null) {
								sel = e;
							}
							cnt++;
						}
					}
					if (cnt > 1) {
						System.out.println("Warning: LL(1) conflict in choice " + body);
					}

				if (sel != null) {
					List<ATSyntaxTree> parse = parse(sel, input, inputIndex);
					if (fullTree && !parse.isEmpty()) {
						ATSyntaxTree root = ATSyntaxTree.treeOf(element, inputIndex.get());
						root.addChildren(parse);
						return ATSyntaxTree.listOf(root);
					} else {
						return parse;
					}
				} else {
					return ATSyntaxTree.createList();
				}
			}

			@Override
			public List<ATSyntaxTree> visitOption(ATOption element) {
				Set<ATTerminal> first = firstSet(element.getBody());
				if (first.contains(la)) {
					List<ATSyntaxTree> parse = parse(element.getBody(), input, inputIndex);
					if (fullTree && !parse.isEmpty()) {
						ATSyntaxTree root = ATSyntaxTree.treeOf(element, inputIndex.get());
						root.addChildren(parse);
						return ATSyntaxTree.listOf(root);
					} else {
						return parse;
					}
				} else {
					ATSyntaxTree root = ATSyntaxTree.treeOf(element, -1);
					return ATSyntaxTree.listOf(root);
				}
			}
		};
		return body.accept(v);
	}

	private Set<ATTerminal> firstSet(ATElement e) {
		return grammar.firstSet(e);
	}

	private ATRule findRuleOf(ATNonTerminal symbol) {
		ATRule rule = grammar.findRuleOf(symbol);
		if (rule != null) {
			return rule;
		} else {
			throw parseError("No rule for NT symbol " + symbol);
		}
	}

	private RuntimeException parseError(String msg) {
		return new RuntimeException(msg);
	}

}
