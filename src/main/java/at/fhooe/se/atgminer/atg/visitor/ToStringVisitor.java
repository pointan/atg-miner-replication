package at.fhooe.se.atgminer.atg.visitor;

import at.fhooe.se.atgminer.atg.*;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.util.BashFormatter;
import at.fhooe.se.atgminer.util.StringUtil;

import java.util.Comparator;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * <p>A visitor that creates a string representation out of various grammar elements. This implementation enables
 * instead of directly using toString, to overwrite specific methods, and change the print behaviour of some
 * elements.</p>
 */
public class ToStringVisitor extends ThrowingATElementVisitor<String> {

    /**
     * A global instance that is used inside the elements toString methods.
     */
    public static final ToStringVisitor INSTANCE = new ToStringVisitor(new BashFormatter(false));
    public static final ToStringVisitor INSTANCE_BASH = new ToStringVisitor(new BashFormatter(true));

    private final BashFormatter formatter;

    private int indentation = 0;

    /**
     * Protected constructor to derive from this class and overwrite specific methods
     */
    protected ToStringVisitor(BashFormatter formatter) {
        super("No toString Method provided for type %s");
        this.formatter = formatter;
    }

    @Override
    public String visitGrammar(ATGrammar grammar) {
        StringBuilder str = new StringBuilder();
        str.append(formatter.purple(String.format("Grammar(%s):", StringUtil.upperCaseFirst(grammar.getStartSymbol().getKey())))).append("\n");
        if (!grammar.getGlobalFunctions().isEmpty()) {
            str.append("GLOBALSEM").append("\n");
            grammar.getGlobalFunctions().stream().map(Object::toString).map(x -> x + "\n").forEach(str::append);
            str.append("END GLOBALSEM").append("\n");
        }
        for (ATRule e : grammar.getRuleList()) {
            str.append(e.accept(this));
            str.append("\n");
        }
        return str.toString();
    }

    @Override
    public String visitRule(ATRule rule) {
        return String.format("%s = %s.", rule.getKey().accept(this), rule.getBody().accept(this));
    }

    @Override
    public String visitTerminal(ATTerminal symbol) {
        return combineWithSemantics(symbol, "'" + symbol.getToken() + "'");
    }

    @Override
    public String visitTerminalClass(ATTerminalClass symbol) {
        return combineWithSemantics(symbol, symbol.getKey());
    }

    @Override
    public String visitNonTerminal(ATNonTerminal symbol) {
        String res = "";
        if (!symbol.getPreSemantics().isEmpty())
            res += formatter.red(" <" + BaseSemantic.toString(symbol.getPreSemantics()) + "> ");
        res += formatter.bold(StringUtil.upperCaseFirst(symbol.getKey()));
        if (!symbol.getInheritedAttributes().isEmpty())
            res += formatter.blue("" + symbol.getInheritedAttributes().stream().map(x -> "↓" + x.toString()).collect(Collectors.joining(" ")));
        if (!symbol.getSynthesizedAttributes().isEmpty())
            res += formatter.green("" + symbol.getSynthesizedAttributes().stream().map(x -> "↑" + x.toString()).collect(Collectors.joining(" ")));
        if (!symbol.getPostSemantics().isEmpty())
            res += formatter.red(" <" + BaseSemantic.toString(symbol.getPostSemantics()) + "> ");
        return res;
    }

    @Override
    public String visitSequence(ATSequence element) {
        boolean needParenthesis = element.getSeq().size() > 1 || element.getSeq().stream().flatMap(ATElement::stream).filter(e -> e instanceof ATNonTerminal || e instanceof ATTerminal).count() > 1;

        StringJoiner join = new StringJoiner(" ");
        if (needParenthesis) join.add(formatter.yellow("(") + formatter.nl());
        for (ATElement e : element.getSeq()) {
            indentation++;
            join.add(formatter.indent(indentation) + e.accept(this) + formatter.nl());
            indentation--;
        }
        if (needParenthesis) join.add(formatter.indent(indentation) + formatter.yellow(")"));
        String key = join.toString();
        return combineWithSemantics(element, key);
    }

    @Override
    public String visitOption(ATOption element) {
        String key = formatter.yellow("[ ") + element.getBody().accept(this) + formatter.yellow(" ]");
        return combineWithSemantics(element, key);
    }

    @Override
    public String visitRepetition(ATRepetition element) {
        String key;
        if (element.getBody() != null) {
            key = formatter.yellow("{ ") + element.getBody().accept(this) + formatter.yellow(" }");
        } else {
            key = formatter.yellow("{ }");
        }
        return combineWithSemantics(element, key);
    }

    @Override
    public String visitChoice(ATChoice element) {
        String key = element.getElements().stream()
                .sorted(Comparator.comparing(ATElement::getKey))
                .map(x -> x.accept(this))
                .collect(Collectors.joining(formatter.nl() + formatter.indent(indentation) + formatter.yellow(" | ")));
        return combineWithSemantics(element, formatter.yellow("(") + key + formatter.yellow(")"));
    }

    @Override
    public String visitTerminalGroup(ATTerminalGroup group) {
        String key = "*" + group.getKey() + "*";
        return combineWithSemantics(group, key);
    }

    private String combineWithSemantics(ATElement element, String key) {
        String res = "";
        if (!element.getPreSemantics().isEmpty())
            res += formatter.red(" <" + BaseSemantic.toString(element.getPreSemantics()) + "> ");
        res += key;
        if (!element.getPostSemantics().isEmpty())
            res += formatter.red(" <" + BaseSemantic.toString(element.getPostSemantics()) + "> ");
        return res;
    }
}
