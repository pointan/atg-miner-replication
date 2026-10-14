package at.fhooe.se.atgminer.atg.semantics;

import java.util.Objects;

import at.fhooe.se.atgminer.atg.ATElement;
import at.fhooe.se.atgminer.concept.v2.AttributeName;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * Represents an attribute of a grammar symbol, e.g. of a nonterminal symbol.
 * The attribute is connected with the grammar symbol to allow different
 * notations. The following notation represent the attribute with name
 * {@code T2}, symbol {@code Term} and subscript {@code 2}.
 *
 * <pre>
 * ...Term<sub>2</sub> sem x = T(Term<sub>2</sub>) endsem...
 * ...Term<sub>2</sub> sem x = Term<sub>2</sub>.T endsem...
 * ...Term<sub>T2</sub> sem x = T<sub>2</sub> endsem...
 * </pre>
 */
public class SubscriptAttribute extends InOutAttribute {

	private final static BaseSemantic DUMMY = new VariableSemantic(null, "dummy");

	private final ATElement symbol;
	private final int symbolSubscript;

	/**
	 * Constructs a new attribute with given name, symbol, and subscript. Note that
	 * only the attribute name is used for analysis algorithms; symbol and subscript
	 * are used for display reasons only
	 *
	 * @param symbol          the owner (e.g. a nonterminal) of this attribute
	 * @param symbolSubscript the subscript of the owner
	 * @param name            the attribute name
	 */
	public SubscriptAttribute(ATElement symbol, int symbolSubscript, String name) {
		super(null, DUMMY, name);
		this.symbol = Objects.requireNonNull(symbol);
		this.symbolSubscript = symbolSubscript;
	}

	public SubscriptAttribute(InterpreterNode node, ATElement symbol, int symbolSubscript, String name) {
		super(node, DUMMY, name);
		this.symbol = Objects.requireNonNull(symbol);
		this.symbolSubscript = symbolSubscript;
	}

	public SubscriptAttribute shallowCopy() {
		return new SubscriptAttribute(getBasedOn(), symbol, symbolSubscript, getName());
	}

	public String toIndexedAttributeString() {
		return subscriptIndex(removeIndex(getName()), symbolSubscript);
	}

	public String toKnuthString() {
		return String.format("%s(%s)", removeIndex(getName()), subscriptIndex(symbol.getKey(), symbolSubscript));
	}

	public String toCompoundString() {
		return String.format("%s.%s", subscriptIndex(symbol.getKey(), symbolSubscript), removeIndex(getName()));
	}

	private String subscriptIndex(String str, int index) {
		return str + AttributeName.subscriptString(index);
	}

	public static String removeIndex(String str) {
		while (str.length() > 1 && Character.isDigit(str.charAt(str.length() - 1))) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	public int getSubscript() {
		return symbolSubscript;
	}

	@Override
	public <T> T accept(SemanticVisitor<T> visitor) {
		return visitor.visitSubscriptAttribute(this);
	}

}
