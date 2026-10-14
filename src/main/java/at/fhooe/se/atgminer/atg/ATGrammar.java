package at.fhooe.se.atgminer.atg;

import at.fhooe.se.atgminer.atg.coco.CocoGrammarParser;
import at.fhooe.se.atgminer.atg.impl.FirstSetVisitor;
import at.fhooe.se.atgminer.atg.impl.GrammarParser;
import at.fhooe.se.atgminer.atg.semantics.DefSemantic;
import at.fhooe.se.atgminer.atg.visitor.ToStringVisitor;
import at.fhooe.se.atgminer.util.StreamUtil;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An ATGrammar object represents a <em>Context Free Grammar</em> with
 * <ol>
 * <li>a start symbol,</li>
 * <li>a set of grammar rules,</li>
 * <li>a set of terminal symbols, and</li>
 * <li>a set of non-terminal symbols.</li>
 * </ol>
 *
 * Grammar elements have optional semantic actions and non-terminal have
 * optional attributes.
 */
public class ATGrammar {

	private ATNonTerminal start;
	private final List<ATRule> ruleList;
	private Set<DefSemantic> globalFunctions = new HashSet<>();

	public ATGrammar() {
		this.ruleList = new ArrayList<>();
	}

	/**
	 * Constructs a new grammar object from the given file . Only grammar elements
	 * are parsed while attributes and semantic actions are ignored. This may change
	 * in future versions.
	 *
	 * @param fileName the name of grammar file
	 * @return a new grammar object representing the content of the given file
	 */
	public static ATGrammar fromFile(String fileName) {
		var parser = new GrammarParser();
		try (InputStream in = new FileInputStream(new File(fileName))) {
			return parser.readGrammar(in);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Constructs a new grammar object from the given file in the Coco/R format.
	 * Only grammar elements are parsed while attributes and semantic actions are
	 * ignored. This may change in future versions.
	 *
	 * @see {@link ssw.jku.at/Research/Projects/Coco/}
	 *
	 * @param fileName the name of the Coco/R grammar file
	 * @return a new grammar object representing the content of the given file or
	 *         {@code null} in case of a parse error
	 */
	public static ATGrammar fromCocoFile(String fileName) {
		var parser = new CocoGrammarParser();
		return parser.parse(fileName);
	}

	public static <T> Set<String> keySet(List<? extends ATElement> symbolList) {
		Set<String> keys = new HashSet<>();
		symbolList.forEach((e) -> keys.add(e.getKey()));
		return keys;
	}

	@Override
	public String toString() {
		return this.accept(ToStringVisitor.INSTANCE);
	}

	public String toStringForBash() {
		return this.accept(ToStringVisitor.INSTANCE_BASH);
	}

	public void setStartSymbol(ATNonTerminal start) {
		this.start = start;
	}

	public ATNonTerminal getStartSymbol() {
		return start;
	}

	public ATNonTerminal createNonTerminal(String key) {
		return new ATNonTerminal(key);
	}

	public ATSequence createSequence() {
		return new ATSequence();
	}

	public ATSequence createSequence(ATElement... seq) {
		return new ATSequence(seq);
	}

	public ATChoice createChoice(ATElement... choices) {
		return new ATChoice(choices);
	}

	public ATRepetition createRepetition(ATElement body) {
		return new ATRepetition(body);
	}

	public ATOption createOption(ATElement body) {
		return new ATOption(body);
	}

	public ATTerminal createTerminal(String key) {
		if(key.equals("\\\\")) { // just a quickfix, as I was not able to find the issue in the CocoGrammarParser, why '\\' becomes the String "\\" and not "\". because using only '\' in the grammar is not valid.
			key = "\\";
		}
		return new ATTerminal(key);
	}

	public ATTerminalClass createTerminalClass(String key, String attributeValue) {
		return new ATTerminalClass(key, attributeValue);
	}

	public ATRule createRule(ATNonTerminal key, ATElement body) {
		return new ATRule(key, body);
	}

    public ATRule createRule(ATNonTerminal key, ATElement body, String sem) {
        return new ATRule(key, body, sem);
    }

	/**
	 * Adds the given rule to the rule list of this grammar.
	 *
	 * @param rule the rule to be added
	 */
	public void addRule(ATRule rule) {
		this.ruleList.add(rule);
	}

	/**
	 * Adds all given rules to the rule list of this grammar.
	 *
	 * @param list the grammar rules to be added
	 */
	public void addRules(ATRule... list) {
		for (ATRule e : list) {
			addRule(e);
		}
	}

	/**
	 * Adds all rules from the given grammar to this grammar. This operation does
	 * not check whether the two grammars have the same start symbol. If the given
	 * grammar object is null, this operation has no effect.
	 *
	 * @param grammar the grammar rules to be added to the rule list of this grammar
	 *                (may be null)
	 */
	public void addRule(ATGrammar grammar) {
		if (grammar != null) {
			for (ATRule e : grammar.getRuleList()) {
				addRule(e);
			}
		}
	}

	public Set<ATTerminal> firstSet(ATElement e) {
		var v = new FirstSetVisitor(this);
		return e.accept(v);
	}

	/**
	 * Searches for and returns the first rule associated with the specified
	 * non-terminal symbol in the grammar. It returns the first rule that matches
	 * the specified non-terminal symbol.
	 *
	 * If no rule with the specified non-terminal symbol is found in the grammar,
	 * the method returns {@code null}.
	 *
	 * @param symbol the ATNonTerminal symbol for which to find the associated rule
	 * @return the ATRule associated with the specified non-terminal symbol if such
	 *         a rule exists within the grammar; {@code null} otherwise.
	 */
	public ATRule findRuleOf(ATNonTerminal symbol) {
		for (ATRule e : ruleList) {
			if (e.getKey().equals(symbol)) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Retrieves all rules associated with a given non-terminal symbol within the
	 * grammar. This method searches through the grammar's list of rules and
	 * collects those rules whose keys (non-terminal symbols) match the specified
	 * symbol.
	 *
	 * The method returns a list of ATRule objects, each representing a rule
	 * associated with the specified non-terminal symbol. If no rules are found for
	 * the symbol, the method returns an empty list.
	 *
	 * @param symbol the ATNonTerminal symbol for which to find all associated rules
	 * @return a List of ATRule objects representing the rules in the grammar that
	 *         have the specified non-terminal symbol as their key. If no such rules
	 *         exist, returns an empty list.
	 */
	public List<ATRule> rulesOfKey(ATNonTerminal symbol) {
		var result = new ArrayList<ATRule>();
		for (ATRule e : ruleList) {
			if (e.getKey().equals(symbol)) {
				result.add(e);
			}
		}
		return result;
	}

	/**
	 * Searches for and returns the first rule associated with the specified
	 * non-terminal symbol in the grammar. It returns the first rule that matches
	 * the specified non-terminal symbol.
	 *
	 * If no rule with the specified non-terminal symbol is found in the grammar,
	 * the method returns {@code null}.
	 *
	 * @param nonTerminalSymbol the symbol key for which to find the associated rule
	 * @return the ATRule associated with the specified non-terminal symbol if such
	 *         a rule exists within the grammar; {@code null} otherwise.
	 */
	public ATRule findRuleOf(String nonTerminalSymbol) {
		for (ATRule e : ruleList) {
			if (e.getKey().getKey().equals(nonTerminalSymbol)) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Removes a specific rule from the grammar. This method takes an ATRule object
	 * as its parameter and removes it from the grammar's list of rules. If the
	 * specified rule is not present in the grammar, this method has no effect. This
	 * operation supports modifying the grammar by directly removing rules based on
	 * their object reference.
	 *
	 * @param r the ATRule to be removed from the grammar
	 */
	public boolean removeRule(ATRule r) {
		return ruleList.remove(r);
	}

	/**
	 * Removes all rules associated with a specific non-terminal symbol from the
	 * grammar. This method looks for a rule that has the specified non-terminal
	 * symbol as its key. If such a rule is found, it is removed from the grammar.
	 *
	 * If no rule associated with the specified non-terminal symbol exists, the
	 * grammar remains unchanged.
	 *
	 * @param e the ATNonTerminal symbol of the rule to be removed
	 */
	public void removeRules(ATNonTerminal e) {
		var rule = findRuleOf(e);
		var removed = removeRule(rule);
		while (removed) {
			rule = findRuleOf(e);
			removed = removeRule(rule);
		}
	}

	/**
	 * Returns the list of rules currently defined in the grammar. This method
	 * provides access to the grammar's internal list of ATRules, allowing external
	 * entities to view or iterate over the rules that comprise the grammar.
	 * Modifications to the returned list do not affect the original grammar's rule
	 * set, ensuring the encapsulation of the grammar's data.
	 *
	 * @return a List of ATRule objects representing the rules currently in the
	 *         grammar
	 */
	public List<ATRule> getRuleList() {
		return ruleList;
	}

	/**
	 * Provides a sorted list of terminal symbols part of this grammar object. Note
	 * that the result is determined by terminal symbols part of the grammar rules
	 * and is independent of the symbols created with method createTerminal.
	 *
	 * @return a sorted list of terminal symbols that appear in this grammar
	 */
	public List<ATTerminal> getTerminalList() {
		final var tSet = new HashSet<ATTerminal>();

		var tVisitor = new AbstractATElementVisitor<Void>() {

			@Override
			public Void visitTerminal(ATTerminal symbol) {
				tSet.add(symbol);
				return null;
			}

			@Override
			public Void visitTerminalClass(ATTerminalClass symbol) {
				tSet.add(symbol);
				return null;
			}
		};

		traversePreOrder(tVisitor);

		List<ATTerminal> tList = new ArrayList<>(tSet);
		Collections.sort(tList);
		return tList;
	}

	/**
	 * Provides a sorted list of non-terminal symbols part of this grammar object.
	 *
	 * @return a sorted list of non-terminal symbols that appear in this grammar
	 */
	public List<ATNonTerminal> getNonTerminalList() {
		final var ntSet = new HashSet<ATNonTerminal>();

		var ntVisitor = new AbstractATElementVisitor<Void>() {

			@Override
			public Void visitNonTerminal(ATNonTerminal symbol) {
				ntSet.add(symbol);
				return super.visitNonTerminal(symbol);
			}
		};
		traversePreOrder(ntVisitor);

		List<ATNonTerminal> ntList = new ArrayList<>(ntSet);
		Collections.sort(ntList);
		return ntList;
	}

	/**
	 * Traverse elements part of this grammar in pre-order (or depth-first order)
	 * with the given visitor object.
	 *
	 * @param <T>     the parameter type of the visitor
	 * @param visitor the visitor object applied to all grammar elements
	 */
	public <T> void traversePreOrder(AbstractATElementVisitor<T> visitor) {
		PreOrderElementTraversal t = new PreOrderElementTraversal();
		for (ATRule e : getRuleList()) {
			e.getKey().accept(visitor);
			t.traverse(e.getBody(), visitor);
		}
	}

	/**
	 * Returns a copy of this grammar with renamed non-terminal symbols.
	 *
	 * @param oldKey the key of the non-terminal symbol in this grammar to be
	 *               renamed
	 * @param newKey the key of the non-terminal symbol in the new grammar
	 * @return a copy of this grammar with renamed non-terminal symbols
	 */
	public ATGrammar rename(String oldKey, String newKey) {
		Map<String, String> keyTable = new HashMap<>();
		keyTable.put(oldKey, newKey);
		RenameNonTerminalSymbolVisitor copy = new RenameNonTerminalSymbolVisitor(keyTable);
		return copy.apply(this);
	}

	public boolean containsRule(String key) {
		return getRuleList().stream().map(ATRule::getKey).map(ATNonTerminal::getKey).anyMatch(key::equals);
	}

	public List<ATTerminal> createTokenSequenceFromString(String inputStr) {
		List<ATTerminal> tokenSeq = new ArrayList<>();
		for (char c : inputStr.toCharArray()) {
			tokenSeq.add(createTerminal(String.valueOf(c)));
		}
		return tokenSeq;
	}

	public <T> T accept(AbstractATElementVisitor<T> v) {
		return v.visitGrammar(this);
	}

	public double usageCoverage() {
		List<ATElement> allTsAndNts = getRuleList().stream().map(ATRule::getBody).flatMap(ATElement::stream)
				.filter(StreamUtil.or(ATTerminal.class::isInstance, ATNonTerminal.class::isInstance))
				.collect(Collectors.toList());
		long covered = allTsAndNts.stream().filter(ATElement::isWasUsed).count();
		System.out.println(covered + " of " + allTsAndNts.size());
		return (double) covered / (double) allTsAndNts.size();
	}

	public ATGrammar toBNFGrammar() {
		ATGrammar resultGrammar = new ATGrammar();

		AbstractATElementVisitor<ATElement> visitor = new ThrowingATElementVisitor<>() {

			private String createName(ATElement element, String baseName) {
				String name;
				if (element instanceof ATNonTerminal) {
					name = element.getKey() + "-" + baseName.substring(0, 3);
				} else {
					name = baseName;
				}

				String newName = name;

				List<ATNonTerminal> existingNTs = new ArrayList<>(ATGrammar.this.getNonTerminalList());
				existingNTs.addAll(resultGrammar.getNonTerminalList());

				List<String> collect = existingNTs.stream().map(ATElement::getKey).collect(Collectors.toList());
				for (int idx = 1; collect.contains(newName); idx++)
					newName = name + "-" + idx;

				return newName;
			}

			@Override
			public ATElement visitTerminal(ATTerminal symbol) {
				return symbol;
			}

			@Override
			public ATElement visitTerminalClass(ATTerminalClass symbol) {
				return symbol;
			}

			@Override
			public ATElement visitNonTerminal(ATNonTerminal symbol) {
				return symbol;
			}

			@Override
			public ATElement visitSequence(ATSequence element) {
				return resultGrammar
						.createSequence(element.getSeq().stream().map(x -> x.accept(this)).toArray(ATElement[]::new));
			}

			@Override
			public ATElement visitOption(ATOption element) {
				ATChoice choice = resultGrammar.createChoice(ATTerminal.createEpsilon(), element.getBody().accept(this));
				ATNonTerminal nonTerminal = resultGrammar.createNonTerminal(createName(element.getBody(), "option"));
				resultGrammar.addRule(resultGrammar.createRule(nonTerminal, choice));
				return nonTerminal;
			}

			@Override
			public ATElement visitRepetition(ATRepetition element) {
				ATElement body = element.getBody().accept(this);
				ATNonTerminal nonTerminal = resultGrammar
						.createNonTerminal(createName(element.getBody(), "Repetition"));
				resultGrammar.addRule(resultGrammar.createRule(nonTerminal,
						resultGrammar.createChoice(body, resultGrammar.createSequence(body, nonTerminal))));

				return nonTerminal;
			}

			@Override
			public ATElement visitChoice(ATChoice element) {
				ATElement[] array = element.getElements().stream().map(x -> x.accept(this)).toArray(ATElement[]::new);
				ATNonTerminal nonTerminal = resultGrammar.createNonTerminal(createName(null, "choice"));
				resultGrammar.addRule(resultGrammar.createRule(nonTerminal, resultGrammar.createChoice(array)));

				return nonTerminal;
			}

			@Override
			public ATElement visitTerminalGroup(ATTerminalGroup group) {
				ATNonTerminal nonTerminal = resultGrammar.createNonTerminal(group.getKey());

				if (!resultGrammar.containsRule(group.getKey())) {
					resultGrammar.addRule(resultGrammar.createRule(nonTerminal, resultGrammar.createChoice(group.getTokens().stream().map(resultGrammar::createTerminal).toArray(ATElement[]::new))));
				}

				return nonTerminal;
			}
		};

		resultGrammar.setStartSymbol(this.getStartSymbol());
		for (ATRule atRule : this.getRuleList()) {
			ATElement newBody;
			if (atRule.getBody() instanceof ATChoice) {
				newBody = resultGrammar.createChoice(
						atRule.getBody().getElements().stream().map(x -> x.accept(visitor)).toArray(ATElement[]::new));
			} else {
				newBody = atRule.getBody().accept(visitor);
			}
			resultGrammar.addRule(resultGrammar.createRule(atRule.getKey(), newBody));
		}

		return resultGrammar;
	}

	public void sortByRuleKey() {
		this.ruleList.sort(new Comparator<ATRule>() {

			@Override
			public int compare(ATRule o1, ATRule o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
	}

	public Set<DefSemantic> getGlobalFunctions() {
		return globalFunctions;
	}

	public void addGlobalFunctions(List<DefSemantic> globalFunctions) {
		this.globalFunctions.addAll(globalFunctions);
	}
}
