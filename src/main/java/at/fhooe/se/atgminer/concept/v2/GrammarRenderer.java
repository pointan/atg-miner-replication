package at.fhooe.se.atgminer.concept.v2;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import at.fhooe.se.atgminer.atg.ATChoice;
import at.fhooe.se.atgminer.atg.ATElement;
import at.fhooe.se.atgminer.atg.AbstractATElementVisitor;
import at.fhooe.se.atgminer.atg.ATGrammar;
import at.fhooe.se.atgminer.atg.ATNonTerminal;
import at.fhooe.se.atgminer.atg.ATOption;
import at.fhooe.se.atgminer.atg.ATRepetition;
import at.fhooe.se.atgminer.atg.ATRule;
import at.fhooe.se.atgminer.atg.ATSequence;
import at.fhooe.se.atgminer.atg.ATTerminal;
import at.fhooe.se.atgminer.atg.ATTerminalClass;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.InOutAttribute;
import at.fhooe.se.atgminer.atg.semantics.SubscriptAttribute;
import at.fhooe.se.atgminer.concept.v2.semantic.SemanticRendererSwitch;

public class GrammarRenderer {

	private SymbolSubscript index;

	public String render(ATGrammar grammar) {
		setSymbolIndex(new SymbolSubscript().fillSubscripts(grammar));
		StringBuilder str = new StringBuilder();
		str.append(String.format("Grammar(%s):", grammar.getStartSymbol().getKey())).append("\n");
		for (ATRule e : grammar.getRuleList()) {
			str.append(renderRule(e));
			str.append("\n");
		}
		return str.toString();
	}

	public String renderRule(ATRule e) {
		StringBuilder str = new StringBuilder();
		str.append(renderNonTerminal(e.getKey()));
		str.append(" = ");
		str.append(renderElement(e.getBody()));
		str.append(".");
		return str.toString();
	}

	public String renderElement(ATElement body) {
		var sw = new AbstractATElementVisitor<String>() {

			@Override
			public String visitChoice(ATChoice element) {
				return renderChoice(element);
			}

			@Override
			public String visitNonTerminal(ATNonTerminal symbol) {
				return renderNonTerminal(symbol);
			}

			@Override
			public String visitOption(ATOption element) {
				return renderOption(element);
			}

			@Override
			public String visitRepetition(ATRepetition element) {
				return renderRepetition(element);
			}

			@Override
			public String visitSequence(ATSequence element) {
				return renderSequence(element);
			}

			@Override
			public String visitTerminal(ATTerminal symbol) {
				return renderTerminal(symbol);
			}

			@Override
			public String visitTerminalClass(ATTerminalClass symbol) {
				return renderTerminalClass(symbol);
			}
		};
		return body.accept(sw);
	}

	public String renderOption(ATOption element) {
		return "[ " + renderElement(element.getBody()) + " ]";
	}

	public String renderSequence(ATSequence element) {
		StringJoiner str = new StringJoiner(" ");
		element.getElements().forEach((e) -> str.add(renderElement(e)));
		return str.toString();
	}

	public String renderTerminal(ATTerminal symbol) {
		String str = "'" + symbol.getToken() + "'";

		if (!symbol.getPostSemantics().isEmpty()) {
			str += renderSemantic(symbol.getPostSemantics());
		}

		return str;
	}

	public String renderTerminalClass(ATTerminalClass symbol) {
		return symbol.getKey();
	}

	public String renderRepetition(ATRepetition element) {
		if (element.getBody() != null) {
			return "{ " + renderElement(element.getBody()) + " }";
		} else {
			return "{ }";
		}
	}

	public String renderNonTerminal(ATNonTerminal symbol) {
		return renderNonTerminal(symbol, index.getSubscriptOf(symbol));
	}

	public String renderNonTerminal(ATNonTerminal symbol, int symbolIndex) {
		return renderNonTerminal(symbol, symbolIndex, symbol.getInheritedAttributes(),
				symbol.getSynthesizedAttributes());
	}

	public String renderNonTerminal(ATNonTerminal symbol, int symbolIndex, Set<InOutAttribute> inheritedAttributes,
			Set<InOutAttribute> synthesizedAttributes) {
		StringBuilder str = new StringBuilder();

		if (!symbol.getPreSemantics().isEmpty()) {
			str.append(renderSemantic(symbol.getPreSemantics()));
		}
		str.append(symbol.getKey());
		str.append(renderAttributeSet(inheritedAttributes, "↓"));
		str.append(renderAttributeSet(synthesizedAttributes, "↑"));
		if (!symbol.getPostSemantics().isEmpty()) {
			str.append(renderSemantic(symbol.getPostSemantics()));
		}

		return str.toString();
	}

	protected String renderChoice(ATChoice element) {
		StringJoiner str = new StringJoiner(" | ");
		element.getElements().stream().sorted(Comparator.comparing(ATElement::getKey))
				.forEach(e -> str.add(renderElement(e)));
		return str.toString();
	}

	public String renderSemantic(BaseSemantic semantic) {
		SemanticRendererSwitch sw = new SemanticRendererSwitch() {

			@Override
			public String caseInOutAttribute(InOutAttribute object) {
				return renderInOutAttribute(object);
			}

		};
		return sw.doSwitch(semantic);
	}

	public String renderSemantic(List<BaseSemantic> semantics) {
		StringJoiner str = new StringJoiner(" ");
		str.add(" <");
		for (var e : semantics) {
			str.add(renderSemantic(e));
		}
		str.add(">");
		return str.toString();
	}

	public String renderAttributeSet(Set<InOutAttribute> set, String prefix) {
		StringBuilder str = new StringBuilder();
		for (var e : set) {
			str.append(prefix);
			str.append(renderAttribute(e));
		}
		return str.toString();
	}

	public String renderAttributeSet(Set<InOutAttribute> set, String prefix, String suffix) {
		StringBuilder str = new StringBuilder();
		for (var e : set) {
			str.append(prefix);
			str.append(renderAttribute(e));
			str.append(suffix);
		}
		return str.toString();
	}

	public String renderAttribute(InOutAttribute attribute) {
		if (attribute instanceof SubscriptAttribute) {
			SubscriptAttribute a = (SubscriptAttribute) attribute;
			return a.toIndexedAttributeString();
		} else {
			return attribute.toString();
		}
	}

	public String renderInOutAttribute(InOutAttribute object) {
		return object.toString();
	}

	public static String subscriptString(int subscript) {
		switch (subscript) {
		case 0:
			return "";
		case 1:
			return "\u2081";
		case 2:
			return "\u2082";
		case 3:
			return "\u2083";
		case 4:
			return "\u2084";
		case 5:
			return "\u2085";
		case 6:
			return "\u2086";
		case 7:
			return "\u2087";
		case 8:
			return "\u2088";
		case 9:
			return "\u2089";
		default:
			return Integer.toString(subscript);
		}
	}

	public static GrammarRenderer getDefaultRenderer() {
		return new GrammarRenderer() {

			@Override
			public String renderInOutAttribute(InOutAttribute object) {
				if (object instanceof SubscriptAttribute) {
					SubscriptAttribute a = (SubscriptAttribute) object;
					return a.toIndexedAttributeString();
				} else {
					return object.toString();
				}
			}

		};
	}

	public static GrammarRenderer getKnuthRenderer() {
		return new GrammarRenderer() {

			@Override
			public String renderNonTerminal(ATNonTerminal symbol, int symbolIndex,
					Set<InOutAttribute> inheritedAttributes, Set<InOutAttribute> synthesizedAttributes) {
				StringBuilder str = new StringBuilder();
				str.append(symbol.getKey());
				str.append(subscriptString(symbolIndex));
				if (!symbol.getPostSemantics().isEmpty()) {
					str.append(renderSemantic(symbol.getPostSemantics()));
				}
				return str.toString();
			}

			@Override
			public String renderInOutAttribute(InOutAttribute object) {
				if (object instanceof SubscriptAttribute) {
					SubscriptAttribute a = (SubscriptAttribute) object;
					return a.toKnuthString();
				} else {
					return object.toString();
				}
			}

		};
	}

	public static GrammarRenderer getCocoRRenderer() {
		return new CocoRGrammarRenderer();
	}

	public static GrammarRenderer getCoco2Renderer() {
		return new Coco2GrammarRenderer();
	}

	public void setSymbolIndex(SymbolSubscript symbolIndex) {
		this.index = symbolIndex;
	}

}
