package at.fhooe.se.atgminer.concept.v2;

import java.util.List;
import java.util.StringJoiner;

import at.fhooe.se.atgminer.atg.ATGrammar;
import at.fhooe.se.atgminer.atg.ATNonTerminal;
import at.fhooe.se.atgminer.atg.ATRule;
import at.fhooe.se.atgminer.atg.semantics.AssignmentSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.InOutAttribute;
import at.fhooe.se.atgminer.concept.v2.semantic.SemanticRendererSwitch;

/**
 * Writes an Attribute Grammar in the input language of Coco2 for Pascal.
 */
public class Coco2GrammarRenderer extends GrammarRenderer {

	@Override
	public String render(ATGrammar grammar) {
		setSymbolIndex(new SymbolSubscript().fillSubscripts(grammar));

		var langName = grammar.getStartSymbol().getKey();
		StringBuilder str = new StringBuilder();
		println(str, "COMPILER ", langName);
		println(str, "NONTERMINALS");

		for (ATRule e : grammar.getRuleList()) {
			println(str, "  " + e.getKey().getKey() + ".");
		}

		println(str, "RULES");

		for (ATRule e : grammar.getRuleList()) {
			println(str);
			str.append(renderRule(e));
			println(str);
		}

		println(str, "END ", langName, ".");
		return str.toString();
	}

	@Override
	public String renderRule(ATRule e) {
		StringBuilder str = new StringBuilder();
		str.append(renderNonTerminalLeftHandSide(e.getKey()));
		str.append(" = ");
		println(str);
		str.append(renderElement(e.getBody()));
		println(str);
		str.append(".");
		return str.toString();
	}

	public String renderNonTerminalLeftHandSide(ATNonTerminal symbol) {
		StringBuilder str = new StringBuilder();

		if (!symbol.getPreSemantics().isEmpty()) {
			str.append(renderSemantic(symbol.getPreSemantics()));
		}
		str.append(symbol.getKey());

		if (symbol.hasAttributes()) {
			str.append("<<");
		}

		str.append(renderAttributeSet(symbol.getInheritedAttributes(), "", ": INTEGER "));
		str.append(renderAttributeSet(symbol.getSynthesizedAttributes(), "VAR ", ": INTEGER"));

		if (symbol.hasAttributes()) {
			str.append(">>");
		}

		if (!symbol.getPostSemantics().isEmpty()) {
			str.append(renderSemantic(symbol.getPostSemantics()));
		}

		return str.toString();
	}

	@Override
	public String renderNonTerminal(ATNonTerminal symbol, int symbolIndex) {
		StringBuilder str = new StringBuilder();

		if (!symbol.getPreSemantics().isEmpty()) {
			str.append(renderSemantic(symbol.getPreSemantics()));
		}
		str.append(symbol.getKey());

		if (symbol.hasAttributes()) {
			str.append("<<");
		}

		str.append(renderAttributeSet(symbol.getInheritedAttributes(), "", ""));
		str.append(renderAttributeSet(symbol.getSynthesizedAttributes(), "", ""));

		if (symbol.hasAttributes()) {
			str.append(">>");
		}

		if (!symbol.getPostSemantics().isEmpty()) {
			str.append(renderSemantic(symbol.getPostSemantics()));
		}

		return str.toString();
	}

	public String renderSemantic(List<BaseSemantic> semantics) {
		StringJoiner str = new StringJoiner(" ");
		str.add(" <<");
		for (var e : semantics) {
			str.add(renderSemantic(e));
			str.add(";");
		}
		str.add(" >>");
		str.add("\n");
		return str.toString();
	}

	public String renderAttribute(InOutAttribute attribute) {
		return attribute.toString();
	}

	@Override
	public String renderSemantic(BaseSemantic semantic) {
		SemanticRendererSwitch sw = new SemanticRendererSwitch() {

			@Override
			public String caseAssignmentSemantic(AssignmentSemantic object) {
				return doSwitch(object.getLeftHandSide()) + " := " + doSwitch(object.getRightHandSide());
			}

//			@Override
//			public String caseInOutAttribute(InOutAttribute object) {
//				return renderInOutAttribute(object);
//			}
//
//			@Override
//			public String caseDivideIntSemantic(DivideIntSemantic object) {
//				return String.format("(%s / %s)", doSwitch(object.getLeftOperand()),
//						doSwitch(object.getRightOperand()));
//			}

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
