package at.fhooe.se.atgminer.concept.v2;

import java.util.List;
import java.util.StringJoiner;

import at.fhooe.se.atgminer.atg.ATGrammar;
import at.fhooe.se.atgminer.atg.ATNonTerminal;
import at.fhooe.se.atgminer.atg.ATRule;
import at.fhooe.se.atgminer.atg.ATTerminal;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.DivideIntSemantic;
import at.fhooe.se.atgminer.atg.semantics.InOutAttribute;
import at.fhooe.se.atgminer.atg.semantics.WriteSemantic;
import at.fhooe.se.atgminer.concept.v2.semantic.SemanticRendererSwitch;

/**
 * Writes an Attribute Grammar in the input language Cocol of Coco/R for Java.
 */
public class CocoRGrammarRenderer extends GrammarRenderer {

	@Override
	public String render(ATGrammar grammar) {
		setSymbolIndex(new SymbolSubscript().fillSubscripts(grammar));

		var langName = grammar.getStartSymbol().getKey();
		StringBuilder str = new StringBuilder();
		println(str, "COMPILER ", langName);
		println(str, "PRODUCTIONS");

		for (ATRule e : grammar.getRuleList()) {
			println(str);
			println(str, renderRule(e));
		}

		println(str, "END ", langName, ".");
		return str.toString();
	}

	@Override
	public String renderRule(ATRule e) {
		StringBuilder str = new StringBuilder();

		println(str, renderNonTerminal(e.getKey()));
		println(str, renderSynthesizedAttributeInit(e.getKey()), "= ");
		println(str, renderElement(e.getBody()));

		str.append(".");
		return str.toString();
	}

	private String renderSynthesizedAttributeInit(ATNonTerminal nts) {
		if (nts.getSynthesizedAttributes().isEmpty()) {
			return "";
		} else {
			StringBuilder str = new StringBuilder();
			str.append(" (. ");
			for (var e : nts.getSynthesizedAttributes()) {
				str.append(e + " = 0; ");
			}
			println(str, ".) ");
			return str.toString();
		}
	}

	@Override
	public String renderNonTerminal(ATNonTerminal symbol, int symbolIndex) {
		StringBuilder str = new StringBuilder();

		if (!symbol.getPreSemantics().isEmpty()) {
			str.append(renderSemantic(symbol.getPreSemantics()));
		}
		str.append(symbol.getKey());

		if (symbol.hasAttributes()) {
			str.append("<");
		}

		str.append(renderAttributeSet(symbol.getInheritedAttributes(), "int "));
		str.append(renderAttributeSet(symbol.getSynthesizedAttributes(), "out int "));

		if (symbol.hasAttributes()) {
			str.append(">");
		}

		if (!symbol.getPostSemantics().isEmpty()) {
			str.append(renderSemantic(symbol.getPostSemantics()));
		}

		return str.toString();
	}

	public String renderSemantic(List<BaseSemantic> semantics) {
		StringJoiner str = new StringJoiner(" ");
		str.add(" (. ");
		for (var e : semantics) {
			str.add(renderSemantic(e));
			str.add(";");
		}
		str.add(" .)\n");
		return str.toString();
	}

	public String renderAttribute(InOutAttribute attribute) {
		return attribute.toString();
	}

	@Override
	public String renderTerminal(ATTerminal symbol) {
		StringBuilder str = new StringBuilder();

		if (!symbol.getPreSemantics().isEmpty()) {
			str.append(renderSemantic(symbol.getPreSemantics()));
		}
		str.append('"');
		str.append(symbol.getToken());
		str.append('"');
		if (!symbol.getPostSemantics().isEmpty()) {
			str.append(renderSemantic(symbol.getPostSemantics()));
		}
		return str.toString();
	}

	@Override
	public String renderSemantic(BaseSemantic semantic) {
		SemanticRendererSwitch sw = new SemanticRendererSwitch() {

			@Override
			public String caseInOutAttribute(InOutAttribute object) {
				return renderInOutAttribute(object);
			}

			@Override
			public String caseDivideIntSemantic(DivideIntSemantic object) {
				return String.format("(%s / %s)", doSwitch(object.getLeftOperand()),
						doSwitch(object.getRightOperand()));
			}

			@Override
			public String caseWriteSemantic(WriteSemantic object) {
				StringJoiner str = new StringJoiner(" + ", "System.out.println(", "); ");
				object.getChildren().forEach((e) -> str.add(doSwitch(e)));
				return str.toString();
			}

		};
		return sw.doSwitch(semantic);
	}

	private void println(StringBuilder out, String... text) {
		for (var e : text) {
			out.append(e);
		}
		out.append("\n");
	}

}
