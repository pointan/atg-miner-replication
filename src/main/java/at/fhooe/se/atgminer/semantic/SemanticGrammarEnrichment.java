package at.fhooe.se.atgminer.semantic;

import at.fhooe.se.atgminer.atg.*;
import at.fhooe.se.atgminer.atg.semantics.*;
import at.fhooe.se.atgminer.atg.semanticsmerge.SemanticMerge;
import at.fhooe.se.atgminer.util.ListUtil;
import at.fhooe.se.atgminer.util.StreamUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Performs an enrichment on grammars, based on the {@link SemanticSyntaxTreeEnrichment} results</p>
 */
public class SemanticGrammarEnrichment {
	private final ATGrammar grammar;

	public SemanticGrammarEnrichment(ATGrammar grammar) {
		this.grammar = grammar;
	}

	public void enrichGrammar(ATSyntaxTree tree) {
		// try to add semantics to the grammar
		Map<Integer, ATElement> keyMapping = tree.stream()
				.map(ATSyntaxTree::getSymbol)
				.collect(Collectors.toMap(ATElement::getID, Function.identity(), StreamUtil.mergeIfEqualOperator()));
		tree.stream()
				.collect(Collectors.groupingBy(x -> x.getSymbol().getID()))
				.forEach((idx, list) -> {
					boolean equal = equalsSemantics(list);
					if (equal) {
						// just directly transfer it to the grammar
						addSyntaxTreeToGrammarElem(list.get(0), keyMapping.get(idx));
					} else {
						Map<Boolean, List<ATSyntaxTree>> matchingOnOptional = list.stream().collect(Collectors.groupingBy(this::nextSiblingIsOptional));
						if (matchingOnOptional.size() != 2) {
							// try to merge semantics:
							SemanticMerge sm = new SemanticMerge();
							// collect and merge presemantics:
							List<BaseSemantic> mergedPreSemantics = list.stream()
									// get pre semantics
									.map(ATSyntaxTree::getPreSemantics)
									// package them into a block semantic
									.map(BlockSemantic::new)
									// cast back to a base semantic
									.map(BaseSemantic.class::cast)
									.reduce(sm::merge)
									// unpack again:
									.map(BaseSemantic::getChildren)
									.orElseThrow();
							// collect and merge postsemantics:
							List<BaseSemantic> mergedPostSemantics = list.stream()
									// get semantics
									.map(ATSyntaxTree::getSemantics)
									// package them into a block semantic
									.map(BlockSemantic::new)
									// cast back to a base semantic
									.map(BaseSemantic.class::cast)
									.reduce(sm::merge)
									// unpack again:
									.map(BaseSemantic::getChildren)
									.orElseThrow();
							// write back into all nodes:
							for (ATSyntaxTree atSyntaxTree : list) {
								atSyntaxTree.getPreSemantics().clear();
								atSyntaxTree.getPreSemantics().addAll(mergedPreSemantics);
								atSyntaxTree.getSemantics().clear();
								atSyntaxTree.getSemantics().addAll(mergedPostSemantics);
							}
							addSyntaxTreeToGrammarElem(list.get(0), keyMapping.get(idx));
						} else if (!matchingOnOptional.values().stream().allMatch(this::equalsSemantics)) {
							throw new IllegalStateException("Different semantics between same blocks");
						} else {
							ATSyntaxTree atSyntaxTree = matchingOnOptional.get(false).get(0);
							addSyntaxTreeToGrammarElem(atSyntaxTree, keyMapping.get(idx));

							// find next grammar element inside optional and add it to the pre semantics
							List<BaseSemantic> semantics = matchingOnOptional.get(true).get(0).getSemantics();
							if (!semantics.isEmpty()) {
								ATElement element = findFirstNtOrTInNextOptional(keyMapping.get(idx));
								element.getPreSemantics().addAll(0, semantics);
							}
						}
					}
				});

		// annotated lhs of rules with attributes
		tree.stream()
				.filter(x -> x.getSymbol() instanceof ATNonTerminal)
				.collect(Collectors.groupingBy(x -> x.getSymbol().getKey()))
				.forEach((ntName, list) -> {
					if (!ListUtil.areEqual(list.stream().map(ATSyntaxTree::getInAttributes).collect(Collectors.toList()))) {
//						System.out.println("WARNING: " + "In Attributes are different for " + ntName);
						throw new IllegalStateException("In Attributes are different for " + ntName);
					}
					if (!ListUtil.areEqual(list.stream().map(ATSyntaxTree::getOutAttributes).collect(Collectors.toList()))) {
						throw new IllegalStateException("Out Attributes are different for " + ntName);
					}
					grammar.findRuleOf(ntName).getKey().getInheritedAttributes().addAll(list.get(0).getInAttributes());
					grammar.findRuleOf(ntName).getKey().getSynthesizedAttributes().addAll(list.get(0).getOutAttributes());
				});

		enrichAny();
		normalizeOutAttributes();
	}

	private void normalizeOutAttributes() {
		for (ATRule atRule : grammar.getRuleList()) {
			Optional<InOutAttribute> outAttribute = atRule.getKey().getSynthesizedAttributes().stream().findFirst();
			if (outAttribute.isEmpty()) continue;
			atRule.getBody().stream().forEach(elem -> {
				Map<BaseSemantic, BaseSemantic> replacements = new HashMap<>();
				for (BaseSemantic postSemantic : elem.getPostSemantics()) {
					if (postSemantic instanceof AssignmentSemantic) {
						AssignmentSemantic assignmentSemantic = (AssignmentSemantic) postSemantic;
						if (assignmentSemantic.getLeftHandSide().equals(outAttribute.get())) {
							replacements.put(postSemantic, new AssignmentSemantic(null, outAttribute.get(), assignmentSemantic.getRightHandSide()));
						}
					}
				}
				replacements.forEach((o, n) -> ListUtil.replace(elem.getPostSemantics(), o, n));
			});
		}
	}

	private void enrichAny() {
		ATRule any = grammar.findRuleOf("Any");
		Optional<InOutAttribute> outAttribute = Optional.ofNullable(any).flatMap(a -> a.getKey().getSynthesizedAttributes().stream().findFirst());
		if (outAttribute.isEmpty()) return;
		any.getBody().stream().filter(ATTerminal.class::isInstance)
				.map(ATTerminal.class::cast)
				.filter(t -> t.getPostSemantics().isEmpty())
				.forEach(terminal -> terminal.getPostSemantics().add(new AssignmentSemantic(null, outAttribute.get(), new ConstSemantic(null, terminal.getToken()))));
	}

	private ATElement findFirstNtOrTInNextOptional(ATElement element) {
		// 1 find parent sequence
		AtomicReference<ATSequence> lastSequence = new AtomicReference<>(null);
		for (ATRule atRule : grammar.getRuleList()) {
			if (atRule.getBody().accept(new ThrowingATElementVisitor<>() {
				@Override
				public Boolean visitTerminal(ATTerminal symbol) {
					return element == symbol;
				}

				@Override
				public Boolean visitTerminalClass(ATTerminalClass symbol) {
					return this.visitTerminal(symbol);
				}

				@Override
				public Boolean visitNonTerminal(ATNonTerminal symbol) {
					return element == symbol;
				}

				@Override
				public Boolean visitSequence(ATSequence element) {
					lastSequence.set(element);
					for (ATElement atElement : element.getSeq()) {
						if (atElement.accept(this)) return true;
					}
					lastSequence.set(null);
					return false;
				}

				@Override
				public Boolean visitOption(ATOption element) {
					return element.getBody().accept(this);
				}

				@Override
				public Boolean visitRepetition(ATRepetition element) {
					return element.getBody().accept(this);
				}

				@Override
				public Boolean visitChoice(ATChoice element) {
					for (ATElement elementElement : element.getElements()) {
						if (elementElement.accept(this)) return true;
					}
					return false;
				}
			})) break;
		}

		if (lastSequence.get() == null) throw new IllegalStateException("Sequence could not be found.");

		// 2 get next element after element
		ATElement nextElem = ListUtil.nextElementInList(lastSequence.get().getSeq(), element).orElseThrow(() -> new IllegalStateException("Could not find any next element"));

		// 3 find first nt or t inside
		return nextElem.accept(new ThrowingATElementVisitor<>() {
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
				return element.getSeq().stream().map(x -> x.accept(this)).findFirst().orElseThrow();
			}

			@Override
			public ATElement visitOption(ATOption element) {
				return element.getBody().accept(this);
			}

			@Override
			public ATElement visitRepetition(ATRepetition element) {
				return super.visitRepetition(element);
			}

			@Override
			public ATElement visitChoice(ATChoice element) {
				return element.getElements().stream().map(x -> x.accept(this)).findFirst().orElseThrow();
			}
		});
	}

	private void addSyntaxTreeToGrammarElem(ATSyntaxTree atSyntaxTree, ATElement symbol) {
		// if the grammar has already semantics, and if the syntax tree would add semantics, we have to check if they
		// are the same, because otherwise it may mean that we have different semantics in the grammar
		if ((!symbol.getPreSemantics().isEmpty() || !symbol.getPostSemantics().isEmpty()) && (!atSyntaxTree.getPreSemantics().isEmpty() || !atSyntaxTree.getSemantics().isEmpty())) {
			if (!(atSyntaxTree.getPreSemantics().equals(symbol.getPreSemantics()) && atSyntaxTree.getSemantics().equals(symbol.getPostSemantics()))) {
				// No we know that at least one element is not equal, check if we can merge it:
				SemanticMerge sm = new SemanticMerge();
				// merge presemantics
				if (atSyntaxTree.getPreSemantics().size() != symbol.getPreSemantics().size()) {
					throw new IllegalStateException("Pre-semantic list sizes do not match");
				} else {
					for (int i = 0; i < atSyntaxTree.getPreSemantics().size(); i++) {
						symbol.getPreSemantics().set(i, sm.merge(symbol.getPreSemantics().get(i), atSyntaxTree.getPreSemantics().get(i)));
					}
				}

				if (atSyntaxTree.getSemantics().size() != symbol.getPostSemantics().size()) {
					throw new IllegalStateException("Post-semantic list sizes do not match");
				} else {
					for (int i = 0; i < atSyntaxTree.getSemantics().size(); i++) {
						symbol.getPostSemantics().set(i, sm.merge(symbol.getPostSemantics().get(i), atSyntaxTree.getSemantics().get(i)));
					}
				}
			}
			// we have nothing to do if we already have data in it.
			return;
		}
		symbol.getPreSemantics().addAll(atSyntaxTree.getPreSemantics());
		symbol.getPostSemantics().addAll(atSyntaxTree.getSemantics());
		if ((!atSyntaxTree.getInAttributes().isEmpty() || !atSyntaxTree.getOutAttributes().isEmpty())) {
			if (!(symbol instanceof ATNonTerminal))
				throw new IllegalStateException("Cannot add attributes to symbol other than NonTerminal Symbol");
			((ATNonTerminal) symbol).getInheritedAttributes().addAll(atSyntaxTree.getInAttributes());
			((ATNonTerminal) symbol).getSynthesizedAttributes().addAll(atSyntaxTree.getOutAttributes());
		}
	}

	private boolean equalsSemantics(List<ATSyntaxTree> list) {
		boolean equal = true;
		ATSyntaxTree atSyntaxTree = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			equal = equal && hasEqualSemantics(atSyntaxTree, list.get(i));
		}
		return equal;
	}

	private boolean hasEqualSemantics(ATSyntaxTree tree1, ATSyntaxTree tree2) {
		return tree1.getSemantics().equals(tree2.getSemantics()) && tree1.getPreSemantics().equals(tree2.getPreSemantics());
	}

	private boolean nextSiblingIsOptional(ATSyntaxTree tree) {
		return tree.getNextSibling()
				.map(ATSyntaxTree::getSymbol)
				.map(this::checkIfSymbolIsInOptional)
				.orElse(false);
	}

	private boolean checkIfSymbolIsInOptional(ATElement outerSymbol) {
		for (ATRule atRule : grammar.getRuleList()) {
			AtomicBoolean isInOption = new AtomicBoolean(false);
			AtomicBoolean found = new AtomicBoolean(false);
			boolean isInTerm = atRule.getBody().accept(new ThrowingATElementVisitor<>() {
				@Override
				public Boolean visitTerminal(ATTerminal symbol) {
					if (symbol == outerSymbol) found.set(true);
					return symbol == outerSymbol && isInOption.get();
				}

				@Override
				public Boolean visitTerminalClass(ATTerminalClass symbol) {
					return this.visitTerminal(symbol);
				}

				@Override
				public Boolean visitNonTerminal(ATNonTerminal symbol) {
					if (symbol == outerSymbol) found.set(true);
					return symbol == outerSymbol && isInOption.get();
				}

				@Override
				public Boolean visitSequence(ATSequence element) {
					return element.getElements().stream().anyMatch(e -> e.accept(this));
				}

				@Override
				public Boolean visitOption(ATOption element) {
					isInOption.set(true);
					boolean res = element.getBody().accept(this);
					isInOption.set(false);
					return res;
				}

				@Override
				public Boolean visitRepetition(ATRepetition element) {
					return element.getBody().accept(this);
				}

				@Override
				public Boolean visitChoice(ATChoice element) {
					return element.getElements().stream().anyMatch(e -> e.accept(this));
				}
			});

			if (found.get() && isInTerm) {
				return true;
			}
		}

		return false;
	}
}
