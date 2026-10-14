package at.fhooe.se.atgminer.parsetree;

import at.fhooe.se.atgminer.atg.*;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.DefNode;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ParseTree implements Iterable<ParseTree> {

	private final String key;
	private final List<ParseTree> children;
	private String label;
	private ParseTree parent;
	private boolean alive;
	private boolean isTerminal;
	private boolean isTerminalClass;
	private boolean removable;
	private String terminalClassAttributeValue;
	private DefNode basedOn;

	private List<ATTerminalGroup> terminalGroups = new ArrayList<>();

	private List<ATTerminal> reachableTerms;
	private final List<BaseSemantic> preSemantics = new ArrayList<>();
	private final List<BaseSemantic> postSemantics = new ArrayList<>();

	/**
	 * The start index of this terminal symbol in the input string (used for
	 * terminal symbols only).
	 */
	private final int inputStartIndex;

	public ParseTree(String key, String label) {
		this(key, label, 0);
	}

	public ParseTree(String key, String label, int inputStartIndex) {
		this.key = key;
		this.label = label;
		this.inputStartIndex = inputStartIndex;
		this.children = new ArrayList<>();
		this.alive = true;
		this.isTerminal = false;
		this.isTerminalClass = false;
		this.removable = true;
	}

	@Override
	public String toString() {
		return asDotString();
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

	private static String id = "A1";

	private String nextSemanticDebugId() {
		String res = id;
		char chr = id.substring(0, 1).charAt(0);
		int num = Integer.parseInt(id.substring(1, 2));
		if (num < 9) {
			num++;
		} else {
			chr++;
			num = 0;
		}

		id = chr + String.valueOf(num);

		return res;
	}

	public void printDot(PrintWriter out) {
		String fontColor = (alive) ? "black" : "gray";

		if (preSemantics.isEmpty() && postSemantics.isEmpty()) {
			 out.format("   %s [label=<%s> fontcolor=%s] %n", key, escape(getInputString()), fontColor);
		} else {
			String preSemanticsStr = preSemantics.stream().map(Objects::toString).collect(Collectors.joining(", "))
                    .replace("\"", "\\\"")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
			if (!preSemanticsStr.isBlank()) {
				preSemanticsStr = "<FONT COLOR=\"Red\">" + preSemanticsStr + "</FONT>";
			}
			String postSemanticsStr = postSemantics.stream().map(Objects::toString).collect(Collectors.joining(", "))
                    .replace("\"", "\\\"")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
			if (!postSemanticsStr.isBlank()) {
				postSemanticsStr = "<FONT COLOR=\"Blue\">" + postSemanticsStr + "</FONT>";
			}
			out.format("   %s [label=<%s%s%s> fontcolor=%s] %n", key, preSemanticsStr, escape(getInputString()), postSemanticsStr, fontColor);
		}

		for (ParseTree e : children) {
			out.format("  %s -> %s [arrowhead=none] %n", key, e.key);
		}

		for (ParseTree e : children) {
			e.printDot(out);
		}
	}

	private String escape(String str) {
		if (str == null) {
			return "";
		} else if (str.length() == 1 && str.charAt(0) == 0) {
			return "NUL";
		} else if (str.contains("<") || str.contains(">")) {
			return str
					.replace("<", "&lt;")
					.replace(">", "&gt;");

		} else {
			str = str.replace("\\", "\\\\");
			str = str.replace("\"", "\\\"");
			return str;
		}
	}

	public ParseTree getParent() {
		return parent;
	}

	private void setParent(ParseTree node) {
		this.parent = node;
	}

	private boolean containsChildWithLabelAndIndex(String label, int idx) {
		for (ParseTree e : children) {
			if (label.equals(e.getLabel()) && e.inputStartIndex == idx) {
				return true;
			}
		}
		return false;
	}

	public boolean addUniqueChild(ParseTree node) {
		if (node.isTerminalSymbol() && node.getLabel().isEmpty()) {
			// if the terminal symbol is ɛ̝ do not add it
			return false;
		}
		if (!containsChildWithLabelAndIndex(node.getLabel(), node.getStartIndex())) {
			addChild(node);
			return true;
		}
		return false;
	}

	/**
	 * Adds the specified {@code ParseTree} node as a child of this node. Upon
	 * addition, it updates the parent reference of the newly added child node to
	 * refer back to this node, ensuring the bidirectional linkage between this node
	 * and its child is established and maintains the integrity of the tree
	 * structure.
	 *
	 * @param node the node to be added as a child. This parameter must not be
	 *             {@code null}.
	 */
	public void addChild(ParseTree node) {
		Objects.requireNonNull(node);
		children.add(node);
		node.setParent(this);
	}

	/**
	 * Adds all {@code ParseTree} nodes in the specified list as children of this
	 * node. Each node in the list is added individually using the
	 * {@link #addChild(ParseTree)} method, which updates the parent reference of
	 * each added child node to refer back to this node, ensuring the bidirectional
	 * linkage between this node and its children is established and maintains the
	 * integrity of the tree structure.
	 *
	 *
	 * @param list the list of nodes to be added as children. The list itself must
	 *             not be {@code null}, and it must not contain any {@code null}
	 *             elements. If the list is empty, this method performs no action.
	 */
	public void addChildren(List<ParseTree> list) {
		for (var e : list) {
			addChild(e);
		}
	}

	public void removeChild(ParseTree node) {
		children.remove(node);
		node.setParent(null);
	}

	public String getLabel() {
		return label;
	}

	/**
	 * Sets the terminal token that is used for input generation. This property is
	 * used for parse tree nodes that represents terminal classes.
	 *
	 * @param value the terminal token used for input generation
	 */
	public void setTerminalClassAttributeValue(String value) {
		this.terminalClassAttributeValue = value;
	}

	/**
	 * Returns the token used for input generation. If this node represents a
	 * terminal symbol, the label is returned; if this node represents a terminal
	 * class the attribute value is used instead.
	 *
	 * @return the terminal token used for input generation
	 */
	public String getInputString() {
		if (isTerminalClass) {
			return terminalClassAttributeValue;
		} else {
			return label;
		}
	}

	public void setLabel(String value) {
		this.label = value;
	}

	public List<ParseTree> getChildren() {
		return children;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean flag) {
		if (removable) {
			this.alive = flag;
		}
		this.alive = flag;
	}

	public boolean isTerminalSymbol() {
		return isTerminal;
	}

	public void setIsTerminal(boolean flag) {
		this.isTerminal = flag;
	}

	public void setIsTerminalClass(boolean flag) {
		this.isTerminal = flag;
		this.isTerminalClass = flag;
	}

	public void setRemovable(boolean flag) {
		this.removable = flag;
	}

	public ATGrammar createGrammar() {
		ATGrammar g = new ATGrammar();
		g.setStartSymbol(g.createNonTerminal(this.label));

		collectRules(g);
		return g;
	}

	private void collectRules(ATGrammar g) {
		ATSequence seq = g.createSequence();

		for (ParseTree e : children) {
			ATElement ate = null;
			if (e.isTerminal && !e.getTerminalGroups().isEmpty()) {
				if (e.getTerminalGroups().size() > 1) {
					// Hex and Alphanum could occur both. But Hex in contained in Alphanum
					if (e.getTerminalGroups().size() == 2 && e.getTerminalGroups().contains(ATTerminalGroup.HEX) && e.getTerminalGroups().contains(ATTerminalGroup.ALPHA_NUM)) {
						ate = ATTerminalGroup.ALPHA_NUM;
					} else {
						throw new IllegalStateException("Parse Tree contains more than one terminal group");
					}
				} else {
					ate = e.getTerminalGroups().get(0);
				}
			} else if (e.isTerminal && e.isTerminalClass) {
				ate = g.createTerminalClass(e.label, e.terminalClassAttributeValue);
			} else if (e.isTerminal) {
				ate = g.createTerminal(e.label);
			} else {
				ate = g.createNonTerminal(e.label);
			}
			seq.add(ate);
		}

		ATRule r = g.createRule(g.createNonTerminal(this.label), seq);
		g.addRule(r);

		for (ParseTree e : children) {
			if (!e.isTerminal) {
				e.collectRules(g);
			}
		}
	}

	public String getKey() {
		return key;
	}

	public void apply(ParseTreeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Iterator<ParseTree> iterator() {
		return new DepthFirstParseTreeIterator(this);
	}

	public Stream<ParseTree> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	/**
	 * Returns a deep copy of the current parse tree.
	 *
	 * @return deep copy of the current tree.
	 */
	public ParseTree copy() {
		var parseTreeMap = stream().map(pt -> {
			ParseTree res = new ParseTree(pt.key, pt.label);
			res.alive = pt.alive;
			res.isTerminal = pt.isTerminal;
			res.isTerminalClass = pt.isTerminalClass;
			res.removable = pt.removable;
			res.terminalClassAttributeValue = pt.terminalClassAttributeValue;
			return res;
		}).collect(Collectors.toMap(ParseTree::getKey, Function.identity()));
		for (ParseTree parseTree : this) {
			var parentNode = parseTreeMap.get(parseTree.key);
			parseTree.getChildren().forEach(child -> parentNode.addChild(parseTreeMap.get(child.key)));
		}
		return parseTreeMap.get(this.key);
	}

	/**
	 * Check if the given parse tree, is anywhere in the parent nodes.
	 *
	 * @param pt The parse tree element that should be if it is a super parent.
	 * @return true if the element is a parent up in the hierarchy, otherwise false.
	 */
	public boolean isSuperParent(ParseTree pt) {
		if (this.getParent() == null)
			return false;
		if (this.getParent() == pt)
			return true;
		return this.getParent().isSuperParent(pt);
	}

	/**
	 * Returns the start index of the terminal symbol represented by this node.
	 *
	 * @return the start index of the terminal symbol represented by this node or
	 *         the value 0 for non-terminal nodes.
	 */
	public int getStartIndex() {
		return inputStartIndex;
	}

	public int getEndIndex() {
		return inputStartIndex + label.length() - 1;
	}

	public void exchangeNodeWithKey(String key, ParseTree newNode) {
		ParseTree oldNode = stream().filter(x -> x.getKey().equals(key)).findFirst().orElseThrow();
		ParseTree oldParent = oldNode.getParent();
		if (oldParent == null) {
			throw new IllegalStateException("No parent found");
		}
		int idxInParent = oldParent.getChildren().indexOf(oldNode);
		if (idxInParent == -1) {
			throw new IllegalStateException("Child node in parent not found - inconsistent parse tree");
		}

		oldParent.getChildren().set(idxInParent, newNode);
	}

	public List<ATTerminalGroup> getTerminalGroups() {
		return terminalGroups;
	}

	public void addTerminalGroup(ATTerminalGroup group) {
		this.terminalGroups.removeAll(group.getBasedOn());
		this.terminalGroups.add(group);
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
		return label.endsWith("_while") || label.contains("_if_");
	}

	public Optional<ParseTree> tryGetFirstChild() {
		if (children.isEmpty()) return Optional.empty();
		return Optional.of(children.get(0));
	}

	public void addPreSemantic(BaseSemantic semantic) {
		this.preSemantics.add(semantic);
	}

	public void addPostSemantic(BaseSemantic semantic){
		this.postSemantics.add(semantic);
	}

	public Optional<ParseTree> tryGetLastChild() {
		if (getChildren().isEmpty()) return Optional.empty();
		return Optional.of(getChildren().get(getChildren().size() - 1));
	}

	/**
	 * Checks if the node is the first child in its parent node
	 * @return true if it is the first child, false if it is not the first child, or if not parent is present
	 */
	public boolean isFirstChild() {
		if (parent == null) return false;
		int pos = parent.getChildren().indexOf(this);
		if (pos == -1) throw new IllegalStateException("Node is not contained in parents child list.");
		return pos == 0;
	}

	/**
	 * Checks if the node is the last child in its parent node
	 * @return true if it is the last child, false if it is not the last child, or if not parent is present
	 */
	public boolean isLastChild() {
		if (parent == null) return false;
		int pos = parent.getChildren().indexOf(this);
		if (pos == -1) throw new IllegalStateException("Node is not contained in parents child list.");
		return pos == parent.getChildren().size() - 1;
	}

	/**
	 * Check if the node is the only child in its parent node
	 * @return true if it is the only child. false if it is not the only child, or if not parent is present
	 */
	public boolean isOnlyChild() {
		if (parent == null) return false;
		int pos = parent.getChildren().indexOf(this);
		if (pos == -1) throw new IllegalStateException("Node is not contained in parents child list.");
		return pos == 0 && parent.getChildren().size() == 1;
	}

	public List<BaseSemantic> getPreSemantics() {
		return preSemantics;
	}

	public List<BaseSemantic> getPostSemantics() {
		return postSemantics;
	}

	public Optional<ParseTree> tryFindNextSibling() {
		if (getParent() == null) return Optional.empty();
		int pos = getParent().getChildren().indexOf(this);
		if (pos == -1) throw new IllegalStateException("Node is not contained in parents child list.");
		if (pos == getParent().getChildren().size() - 1) return Optional.empty();
		return Optional.of(getParent().getChildren().get(pos + 1));
	}

	public Optional<ParseTree> tryFindPreviousAliveSibling() {
		if (getParent() == null) return Optional.empty();
		int pos = getParent().getChildren().indexOf(this);
		if (pos == -1) throw new IllegalStateException("Node is not contained in parents child list.");
		pos--;
		while(pos >= 0) {
			if (getParent().getChildren().get(pos).isAlive()) return Optional.of(getParent().getChildren().get(pos));
			pos--;
		}
		return Optional.empty();
	}

	public boolean containsSemantics() {
		return !getPreSemantics().isEmpty() || !getPostSemantics().isEmpty();
	}

	public boolean containsTerminals() {
		return stream().anyMatch(ParseTree::isTerminalSymbol);
	}

	public boolean isSemOnlyNode() {
		return getLabel().equals("SEM");
	}

	public DefNode getBasedOn() {
		return basedOn;
	}

	public void setBasedOn(DefNode basedOn) {
		this.basedOn = basedOn;
	}
}
