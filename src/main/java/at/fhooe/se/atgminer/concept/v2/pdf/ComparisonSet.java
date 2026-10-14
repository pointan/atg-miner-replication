package at.fhooe.se.atgminer.concept.v2.pdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.fhooe.se.codeanalysis.adele.interpreter.node.ArrayAccessNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ArrayLiteralNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.BinaryExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.EqualNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.NotEqualNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.NotNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ValueLiteralNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.python.SliceNode;
import at.fhooe.se.codeanalysis.adele.interpreter.symbolic.SymbolicObject;

/**
 * A set of all comparisons of program inputs with character literals.
 * Comparisons are collected from following AST patterns:
 * 
 * <pre>
 *   not (c in ['0123456789'])
 *   not (c in ['0', '1', '2', '3'])
 *   not (c == '0')
 *   not (c == 'true')
 * </pre>
 * 
 * with {@code ArrayAccessNode c} or {@code SliceNode c}.
 */
public class ComparisonSet {

	public static class Entry {
		public final int startIndex;
		public final int endIndex;
		public final String literal;

		public Entry(int startIndex, int endIndex, String literal) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.literal = literal;
		}

		@Override
		public String toString() {
			return String.format("%d-%d %s", startIndex, endIndex, literal);
		}
	}

	private final List<Comparison> comps;
	private int maxStartIndex;

	private String acceptedInputPrefix;

	public ComparisonSet() {
		this.comps = new ArrayList<>();
	}

	@Override
	public String toString() {
		var str = new StringBuilder();
		for (int i = 0; i <= maxStartIndex; i++) {
			str.append(String.format("%d %s%n", i, charsAt(i)));
		}
		return str.toString();
	}

	public void clear() {
		comps.clear();
		maxStartIndex = 0;
	}

	public void fillFromPathCondition(List<SymbolicObject> pathCondition) {
		clear();
		for (var e : pathCondition) {
			fillFromNode(e.getNode());
		}
	}

	public List<Entry> randomCharsStartAt(int startIndex) {
		var result = new ArrayList<Entry>();
		for (var e : comps) {
			if (e.startsAt(startIndex)) {
				result.add(new Entry(startIndex, e.getEndIndex(), e.getRandomLiteral()));
			}
		}
		return result;
	}

	public List<Entry> allCharsStartAt(int startIndex) {
		var result = new ArrayList<Entry>();
		for (var e : comps) {
			if (e.startsAt(startIndex)) {
				for (var literal : e.getLiterals()) {
					result.add(new Entry(startIndex, e.getEndIndex(), literal));
				}
			}
		}
		return result;
	}

	public List<Entry> allCharsAt(int index) {
		var result = new ArrayList<Entry>();
		for (var e : comps) {
			if (e.containsIndex(index)) {
				for (var literal : e.getLiterals()) {
					result.add(new Entry(e.getStartIndex(), e.getEndIndex(), literal));
				}
			}
		}
		return shuffle(result);
	}

	public List<Entry> randomCharsAt(int index) {
		var result = new ArrayList<Entry>();
		for (var e : comps) {
			if (e.containsIndex(index)) {
				result.add(new Entry(e.getStartIndex(), e.getEndIndex(), e.getRandomLiteral()));
			}
		}
		return result;
		//return shuffle(result);
	}

	public List<String> charsAt(int startIndex) {
		var result = new ArrayList<String>();
		for (var e : comps) {
			if (e.startsAt(startIndex)) {
				result.addAll(e.getLiterals());
			}
		}
		return result;
	}

	private void fillFromNode(ExpressionNode node) {
		if (node instanceof NotNode) {
			var not = (NotNode) node;
			if (not.getOperand() instanceof BinaryExpressionNode) {
				// Extract index of comparison.
				int startIndex = -1;
				int endIndex = -1;
				var bin = (BinaryExpressionNode) not.getOperand();
				if (bin.getLeft() instanceof ArrayAccessNode) {
					var arrayAccess = (ArrayAccessNode) bin.getLeft();
					if (arrayAccess.getSubscripts().length == 1 && arrayAccess.getSubscripts()[0].isConst()) {
						startIndex = arrayAccess.getSubscripts()[0].execute(null).intValue();
						endIndex = startIndex;
					}
				} else if (bin.getLeft() instanceof SliceNode) {
					var slice = (SliceNode) bin.getLeft();
					if (slice.getBeginExpr() != null && slice.getBeginExpr().isConst()) {
						startIndex = slice.getBeginExpr().execute(null).intValue();
						// In Python, the endIndex is exclusive, hence -1.
						endIndex = slice.getEndExpr().execute(null).intValue() - 1;
					}
				}
				// Extract character literals of comparison.
				var literals = new ArrayList<String>();
				if (bin.getRight() instanceof ValueLiteralNode) {
					if (startIndex == endIndex) {
						var s = trim(bin.getRight().toString());
						if (s.isEmpty()) {
							literals.add(Comparison.EMPTY_LITERAL);
						}
						for (var e : s.toCharArray()) {
							literals.add(String.valueOf(e));
						}
					} else {
						literals.add(trim(bin.getRight().toString()));
					}

				} else if (bin.getRight() instanceof ArrayLiteralNode) {
					for (var e : ((ArrayLiteralNode) bin.getRight()).getElements()) {
						literals.add(trim(e.toString()));
					}
				}

				if (0 <= startIndex) {
					comps.add(new Comparison(startIndex, endIndex, literals));
					maxStartIndex = Math.max(maxStartIndex, startIndex);
				}
			}
		} else if (node instanceof NotEqualNode) {
			var notEqual = (NotEqualNode) node;
			if (notEqual.getLeft() instanceof SliceNode && notEqual.getRight() instanceof ValueLiteralNode) {
				var slice = (SliceNode) notEqual.getLeft();
				if (slice.getBeginExpr() != null && slice.getBeginExpr().isConst()) {
					int startIndex = slice.getBeginExpr().execute(null).intValue();
					int endIndex = slice.getEndExpr().execute(null).intValue() - 1;
					var literal = trim(notEqual.getRight().toString());
					var c = new Comparison(startIndex, endIndex, List.of(literal));
					comps.add(c);
					if (0 <= startIndex) {
						maxStartIndex = Math.max(maxStartIndex, startIndex);
					}
				}
			}
		}
		else if (node instanceof EqualNode) {
			var equal = (EqualNode) node;
			if (equal.getLeft() instanceof ArrayAccessNode && equal.getRight() instanceof ValueLiteralNode) {
				var arrayAccess = (ArrayAccessNode) equal.getLeft();
				if (arrayAccess.getSubscripts().length == 1 && arrayAccess.getSubscripts()[0].isConst()) {
					int startIndex = arrayAccess.getSubscripts()[0].execute(null).intValue();
					int endIndex = startIndex;
					var literal = trim(equal.getRight().toString());
					var c = new Comparison(startIndex, endIndex, List.of(literal));
					comps.add(c);
					if (0 <= startIndex) {
						maxStartIndex = Math.max(maxStartIndex, startIndex);
					}

				}
			}
		}
	}

	private static String trim(String literal) {
		if (literal.length() > 1 && literal.startsWith("'") && literal.endsWith("'")) {
			return literal.substring(1, literal.length() - 1);
		} else {
			return literal;
		}
	}

	private static List<Entry> shuffle(List<Entry> list) {
		if (list.size() <= 1) {
			return list;
		} else {
			var shuffled = new ArrayList<>(list);
			Collections.shuffle(shuffled);
			return shuffled;
		}
	}

	public int getLastIndex() {
		return maxStartIndex;
	}

	public void setAcceptedPrefix(String newValue) {
		acceptedInputPrefix = newValue;
	}

	public String getAcceptedPrefix() {
		return acceptedInputPrefix;
	}

}
