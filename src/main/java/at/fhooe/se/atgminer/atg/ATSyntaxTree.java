package at.fhooe.se.atgminer.atg;

import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.InOutAttribute;
import at.fhooe.se.atgminer.concept.v2.AttributeName;
import at.fhooe.se.atgminer.util.ListUtil;
import at.fhooe.se.codeanalysis.adele.interpreter.node.ExpressionNode;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ATSyntaxTree implements Iterable<ATSyntaxTree> {

	private static int nodeCounter = 1;
	private final int nodeKey;
	private final ATElement symbol;
	private final List<BaseSemantic> preSemantics = new ArrayList<>();
	private final List<BaseSemantic> semantics = new ArrayList<>();
	private final List<InOutAttribute> inAttributes = new ArrayList<>();
	private final List<InOutAttribute> outAttributes = new ArrayList<>();
	private final List<ExpressionNode> assignedConstraints;
	private ATSyntaxTree parent;
	private List<ATSyntaxTree> children;
	private ExpressionNode assignedExpression;

	private List<ATTerminal> reachableTerms;
	private int inputIdx;

	private int symbolIndex;

	public ATSyntaxTree(ATElement symbol, int inputIdx) {
		this.nodeKey = nodeCounter++;
		this.symbol = symbol;
		this.assignedConstraints = new ArrayList<>();
		this.inputIdx = inputIdx;
	}

	public static List<ATSyntaxTree> listOf(ATSyntaxTree... nodes) {
		List<ATSyntaxTree> list = createList();
		list.addAll(Arrays.asList(nodes));
		return list;
	}

	public static List<ATSyntaxTree> listOf(int inputIdx, ATElement... symbols) {
		List<ATSyntaxTree> list = createList();
		for (ATElement e : symbols) {
			list.add(new ATSyntaxTree(e, inputIdx));
		}
		return list;
	}

	public static ATSyntaxTree treeOf(ATElement e, int inputIdx) {
		return new ATSyntaxTree(e, inputIdx);
	}

	public static List<ATSyntaxTree> createList() {
		return new ArrayList<>();
	}

	@Override
	public Iterator<ATSyntaxTree> iterator() {
		final Deque<ATSyntaxTree> stack = new ArrayDeque<>();

		new Object() {
			void buildStack(ATSyntaxTree tree) {
				stack.add(tree);
				tree.getChildren().forEach(this::buildStack);
			}
		}.buildStack(this);

		return new Iterator<>() {
			@Override
			public boolean hasNext() {
				return !stack.isEmpty();
			}

			@Override
			public ATSyntaxTree next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return stack.remove();
			}
		};
	}

	public Stream<ATSyntaxTree> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	public ATElement getSymbol() {
		return symbol;
	}

	@Override
	public String toString() {
		StringWriter str = new StringWriter();
		toString(0, str);
		return str.toString();
	}

	private void toString(int level, StringWriter str) {
		for (int i = 0; i < level; i++) {
			str.append("  ");
		}
		str.append("+-");
		if (!preSemantics.isEmpty()) str.append(" <").append(preSemantics.toString()).append("> ");
		str.append(symbol.toString());
		if (getSymbolIndex() > 0) {
			str.append(AttributeName.replaceSubscripts(Integer.toString(getSymbolIndex())));
		}
		if (!inAttributes.isEmpty()) str.append(" ").append(inAttributes.stream().map(x -> "↓" + x.toString()).collect(Collectors.joining(" ")));
		if (!outAttributes.isEmpty()) str.append(" ").append(outAttributes.stream().map(x -> "↑" + x.toString()).collect(Collectors.joining(" ")));
		if (!semantics.isEmpty()) str.append(" <").append(semantics.toString()).append("> ");
		str.append("\n");
		if (children != null) {
			children.forEach(e -> e.toString(level + 1, str));
		}
	}

	public String asDotString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);

		out.println("digraph ParseTree { ");
		out.println("  node [color=white]; ");
		printDot(out);
		out.println("}");

		return writer.toString();
	}

	public void printDot(PrintWriter out, String fontColor) {
		String label = symbol.getKey().replace("\"", "\\\"").replace("<", "&lt;").replace(">", "&gt;");
		if (getSymbolIndex() > 0) {
			label += AttributeName.replaceSubscripts(Integer.toString(getSymbolIndex()));
		}
		if (symbol instanceof ATNonTerminal) {
			if (!getInAttributes().isEmpty()) {
				label += " " + inAttributes.stream().map(x -> "↓" + x.toString()).collect(Collectors.joining(" "));
			}
			if (!getOutAttributes().isEmpty()) {
				label += " " + outAttributes.stream().map(x -> "↑" + x.toString()).collect(Collectors.joining(" "));
			}
		}
		if (!getSemantics().isEmpty()) {
			label +=  "<FONT COLOR=\"Blue\">" + " (. " + getSemantics() + " .)" + "</FONT>" ;
		}
		if (!getPreSemantics().isEmpty()) {
			label = "<FONT COLOR=\"Red\">" + " (. " + getPreSemantics() + " .)" + "</FONT>" + label;
		}

		out.format("    %s [label=<%s> fontcolor=\"%s\"] %n", nodeKey, label, fontColor);

		for (var e : getChildren()) {
			out.format("    %s -> %s [arrowhead=none  color=\"%s\"] %n", nodeKey, e.nodeKey, fontColor);
		}

		for (var e : getChildren()) {
			e.printDot(out, fontColor);
		}
	}

	public void printDot(PrintWriter out) {
		printDot(out, "black");
	}

	public void addChild(ATSyntaxTree t) {
		getChildren().add(t);
	}

	public List<ATSyntaxTree> getChildren() {
		if (children == null) {
			children = new ArrayList<>();
		}
		return children;
	}

	public void addChildren(List<ATSyntaxTree> list) {
		getChildren().addAll(list);
		list.forEach(x -> x.setParent(this));
	}

	public Optional<ATSyntaxTree> getNextSibling() {
		return ListUtil.nextElementInList(getParent().getChildren(), this);
	}

	public ATSyntaxTree getParent() {
		return parent;
	}

	public boolean isLeaf() {
		return getChildren().isEmpty();
	}

	public boolean isTerminal() {
		boolean leaf = isLeaf();
		boolean term = symbol instanceof ATTerminal;
		if (term && !leaf) {
			throw new IllegalStateException("Syntax Tree is a Terminal, but is not a leaf node");
		}
		return leaf && term;
	}

	private void setParent(ATSyntaxTree parent) {
		this.parent = parent;
	}

	public ExpressionNode getAssignedExpression() {
		return assignedExpression;
	}

	public void setAssignedExpression(ExpressionNode assignedExpression) {
		this.assignedExpression = assignedExpression;
	}

	public List<ExpressionNode> getAssignedConstraints() {
		return assignedConstraints;
	}

	public void addAssignedConstraint(ExpressionNode constraints) {
		this.assignedConstraints.add(constraints);
	}

	public void addSemantic(BaseSemantic semantic) {
		this.semantics.add(semantic);
	}

	public List<BaseSemantic> getSemantics() {
		return semantics;
	}

	public void addPreSemantic(BaseSemantic semantic) {
		this.preSemantics.add(semantic);
	}

	public List<BaseSemantic> getPreSemantics() {
		return preSemantics;
	}

	public void addInAttribute(InOutAttribute in) {
		this.inAttributes.add(in);
	}

	public void addOutAttribute(InOutAttribute out) {
		this.outAttributes.add(out);
	}

	public List<InOutAttribute> getInAttributes() {
		return inAttributes;
	}

	public List<InOutAttribute> getOutAttributes() {
		return outAttributes;
	}

	public int getSymbolIndex() {
		return symbolIndex;
	}

	public void setSymbolIndex(int symbolIndex) {
		this.symbolIndex = symbolIndex;
	}

	public int getNodeKey() {
		return nodeKey;
	}

	public List<ATTerminal> getReachableTerms() {
		if (reachableTerms == null) {
			throw new IllegalStateException("Reachable terms have not been set for this element");
		}
		return reachableTerms;
	}

	public void setReachableTerms(List<ATTerminal> reachableTerms) {
		this.reachableTerms = reachableTerms;
	}

	public boolean isPseudoNode() {
		return !(symbol instanceof ATTerminal || (symbol instanceof ATNonTerminal && !symbol.getKey().equals("Any")));
	}

	public int getInputIdx() {
		return inputIdx;
	}
}
