package at.fhooe.se.atgminer.semantic.v2;

import at.fhooe.se.atgminer.concept.mimid.ATGMinerLabeling;
import at.fhooe.se.atgminer.concept.mimid.Labeling;
import at.fhooe.se.atgminer.parsetree.*;
import at.fhooe.se.codeanalysis.adele.interpreter.AbstractInterpreterListener;
import at.fhooe.se.codeanalysis.adele.interpreter.node.*;
import at.fhooe.se.codeanalysis.adele.interpreter.node.func.StdFuncCallNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.python.SelectNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.python.SliceNode;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.*;
import at.scch.codeanalytics.commons.contracts.Contract;

import java.util.*;
import java.util.logging.Logger;

/**
 * While listening to the symbolic interpreter, objects of this class constructs
 * a parse tree for the input processed by the Python program. Tree nodes are
 * constructed from
 * <ul>
 * <li>function call</li>
 * <li>while statement</li>
 * <li>if statement</li>
 * <li>array access</li>
 * <li>slice</li>
 * <li>identifier</li>
 * </ul>
 * during interpretation. In addition, loop handling requires listening to break
 * and continue statements.
 * <p/>
 * This class uses <em>every access</em> to the input characters to construct
 * potential terminal symbols in the parse tree. In a later step (outside of
 * this class) the actual position of the terminal symbol is computed. All other
 * nodes will be removed.
 * <p/>
 * This implementation also contains a first implementation of the following
 * Mimid rule <em>In both languages, we stop tracking as soon as the input is
 * transformed or copied into a different data structure</em>. This behaviour
 * is implemented in the hooks for the assignment statement and standard
 * function calls.
 * <ol>
 */
public class MimidParseTreeBuilderV2 extends AbstractInterpreterListener {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	protected final static List<String> LEXER_FUNCTIONS = List.of("new_ch", "pos", "len", "getvalue", "skipspaces", "_skip", "next", "next2", "next_ord", "peek", "substr", "getvalue", "read", "__init__", "ABCParse", "Parser", "input");

	private ParseTree root;
	protected final ParseTreeStack nodeStack;
	private final ParseTreeFactory factory;
	private final Labeling labeling;
	private final Set<Integer> ignore;
	private final Set<String> transformingStdFuncNames = new HashSet<String>(List.of("long", "float"));
	private final Map<IfStmtNode, ParseTree> astToParseTreeMap;
	protected ParseTree lastNode;
	private DefNode lastDefNode;

	private final Deque<String> functionStackTrace = new ArrayDeque<>();

	public MimidParseTreeBuilderV2() {
		this.root = null;
		this.nodeStack = new ParseTreeStack();
		this.factory = new ParseTreeFactory();
		this.labeling = new ATGMinerLabeling();
		this.ignore = new HashSet<>();
		this.astToParseTreeMap = new HashMap<>();
	}

	@Override
	public void enterFuncDef(FuncDefNode node) {
		lastDefNode = node;
	}

	@Override
	public void enterMethodDef(MethodDefNode node) {
		lastDefNode = node;
	}

	int idx = 1;
	@Override
	public void enterCallStmt(String funcName) {
		functionStackTrace.push(funcName);
		ParseTree node = factory.createNonTerminal(funcName);
		node.setBasedOn(lastDefNode);
		if (root == null) {
			root = node;
		}

		if (!nodeStack.isEmpty()) {
			nodeStack.peek().addChild(node);
		}
		nodeStack.push(node, ParseTreeType.CALL);

		// Helper for storing semantics:
		ParseTree pt = new ParseTree("semnode"+idx++, "SEM");
		node.addChild(pt);
		lastNode = pt;
	}

	@Override
	public void leaveCallStmt(String funcName) {
		ParseTree callNode = nodeStack.pop();
		if (!funcName.equals(callNode.getLabel())) {
			nodeStack.removeFirstUntil(ParseTreeType.CALL);
			callNode = nodeStack.pop();
		}
		Contract.assertThat(funcName.equals(callNode.getLabel()), "funcName.equals(callNode.getLabel())");
		lastNode = callNode;
		if (!functionStackTrace.pop().equals(funcName)) {
			throw new IllegalStateException("Popping function from function stack is different to expected function.");
		}
	}

	@Override
	public void enterWhile(WhileStmtNode astNode) { // Note: change this to only capture the whole while and not a single loop
		String nodeLabel = labeling.labelOfWhileNode(nodeStack.findFirstOfType(ParseTreeType.CALL).get(), astNode);
		// replace: String nodeLabel =
		// Label.ofWhileNode(nodeStack.findFirstOfType(ParseTreeType.CALL).get());
		ParseTree node = factory.createNonTerminal(nodeLabel);
		if (!nodeStack.isEmpty()) {
			nodeStack.peek().addChild(node);
		} else {
			LOGGER.warning("ENTER WHILE: Empty node stack in enterIfStmt.");
		}
		nodeStack.push(node, ParseTreeType.LOOP);
		// Helper for storing semantics:
		ParseTree pt = new ParseTree("semnode"+idx++, "SEM");
		node.addChild(pt);
		lastNode = pt;
	}

	@Override
	public void leaveWhile(WhileStmtNode node, int iterations) { // Note: change this to only capture the whole while and not a single loop
		ParseTree whileNode = nodeStack.pop();
		if (!whileNode.getLabel().endsWith(Label.WHILE)) {
			nodeStack.removeFirstUntil(ParseTreeType.LOOP);
			whileNode = nodeStack.pop();
		}
		Contract.assertThat(whileNode.getLabel().endsWith(Label.WHILE), "whileNode.getLabel().endsWith(Label.WHILE)");
		lastNode = whileNode;
	}

	@Override
	public void enterIfStmt(IfStmtNode stmt) {
		ParseTree parent = nodeStack.findFirstOfType(ParseTreeType.CALL).get();
		String nodeLabel = labeling.labelOfIfNode(parent, stmt);
		ParseTree node = factory.createNonTerminal(nodeLabel);
		astToParseTreeMap.put(stmt, node);
		if (!nodeStack.isEmpty()) {
			nodeStack.peek().addChild(node);
		} else {
			LOGGER.warning("ENTER IF: Empty node stack.");
		}
		nodeStack.push(node, ParseTreeType.IF);
		ParseTree pt = new ParseTree("semnode"+idx++, "SEM");
		node.addChild(pt);
		lastNode = pt;
	}

	@Override
	public void enterIfStmt(IfStmtNode node, ExpressionNode conditionExpression, RuntimeObject condition) {
		// Update label with branch suffix 0 or 1.
		var ifNode = astToParseTreeMap.get(node);
		if (ifNode != null) {
			ifNode.setLabel(ifNode.getLabel() + Label.ofIfCondition(condition));
		} else {
			LOGGER.warning("ENTER IF: Hook is called with unknown node");
		}
	}

	@Override
	public void leaveIfStmt(IfStmtNode stmt, ExpressionNode conditionExpression, RuntimeObject condition) {
		ParseTreeType ifNodeType = nodeStack.peekType();
		ParseTree ifNode = nodeStack.pop();
		if (ifNode != null) {
			if (ifNodeType != ParseTreeType.IF) {
				LOGGER.warning("LEAVE IF: Inconsistent node stack, top " + ifNode.getLabel() + ".");
			}
		}
		lastNode = ifNode;
	}

	@Override
	public void leaveBreakStmt() {
		nodeStack.removeFirstInclusive(ParseTreeType.LOOP);
	}

	@Override
	public void leaveContinueStmt() {
		nodeStack.removeFirstUntil(ParseTreeType.LOOP);
	}

	@Override
	public void leaveIdentifier(IdentifierNode node, boolean isConst, RuntimeObject value) {
		// Stop tracking when input is transformed or copied into a different data
		// structure. Maybe we should clear the symbolic object on the runtime object to
		// stop further tracking.
		if (ignore.contains(node.getNodeID())) {
			return;
		}

		leaveIdentifier(value);
	}

	public void leaveIdentifier(QualifiedIdentifierNode node, boolean isConst, RuntimeObject value) {
		// Stop tracking when input is transformed or copied into a different data
		// structure. Maybe we should clear the symbolic object on the runtime object to
		// stop further tracking.
		if (ignore.contains(node.getNodeID())) {
			return;
		}

		leaveIdentifier(value);
	}

	private void leaveIdentifier(RuntimeObject value) {
		ExpressionNode syNode = value.getSymbolicObject().getNode();
		var aaccess = toArrayAccessNode(syNode);
		if (aaccess != null) {
			int index = executeConst(aaccess.getSubscripts()[0]).intValue();
			ParseTree terminalSymbol = factory.createTerminal(value.stringValue(), index);
			if (nodeStack.peek().addUniqueChild(terminalSymbol)) {
				lastNode = terminalSymbol;
			}
		} else {
			var slice = toSliceNode(syNode);
			if (slice != null) {
				int index = executeConst(slice.getBeginExpr()).intValue();
				ParseTree terminalSymbol = factory.createTerminal(value.stringValue(), index);
				if (nodeStack.peek().addUniqueChild(terminalSymbol)) {
					lastNode = terminalSymbol;
				}
			}
		}
	}

	// Must be in sync with method leaveIdentifier. SliceNode need not be handled
	// because SliceNode informs listener by this method. See method
	// SliceNode.attachSymbol.
	@Override
	public void leaveArrayAccess(ExpressionNode node, ValueObject subscript, RuntimeObject element) {
		// Stop tracking when input is transformed or copied into a different data
		// structure. Maybe we should clear the symbolic object on the runtime object to
		// stop further tracking.

		if (ignore.contains(node.getNodeID())) {
			return;
		}

		ExpressionNode syNode = element.getSymbolicObject().getNode();
		var aaccess = toArrayAccessNode(syNode);
		if (aaccess != null) {
			int index = executeConst(aaccess.getSubscripts()[0]).intValue();
			ParseTree terminalSymbol = factory.createTerminal(element.stringValue(), index);
			if (nodeStack.peek().addUniqueChild(terminalSymbol)) {
				lastNode = terminalSymbol;
			}
		} else {
			var slice = toSliceNode(syNode);
			if (slice != null) {
				int index = executeConst(slice.getBeginExpr()).intValue();
				ParseTree terminalSymbol = factory.createTerminal(element.stringValue(), index);
				if (nodeStack.peek().addUniqueChild(terminalSymbol)) {
					lastNode = terminalSymbol;
				}
			}
		}
	}

	@Override
	public void enterAssignStmt(LValueNode lhs, ExpressionNode rhs) {
		if (isLexerFunction()) return;
		// If rhs is binary expression, input is transform. Filter transforming
		// expressions only and do not include comparison expression.
		if (rhs instanceof BinaryExpressionNode) {
			for (var e : rhs) {
				if (inputAccessIsPossible(e)) {
					ignore.add(e.getNodeID());
				}
			}
		}
		// Stop tracking at assignment to complex data structure (e.g. result[key] =
		// value) or when input is used in the left hand side.
		if (lhs instanceof SelectNode) {
			for (var e : rhs) {
				// Do not filter input access nodes here (inputAccessIsPossible)!
				ignore.add(e.getNodeID());
			}
			for (var e : nodesOf(lhs)) {
				ignore.add(e.getNodeID());
			}
		}

		// Add more AST patterns here for stopping input character tracking.
	}

	@Override
	public void leaveAssignStmt(LValueNode lhs, ExpressionNode rhs, RuntimeObject rhsObj) {
		ignore.clear();
	}

	@Override
	public void enterStdFuncCall(StdFuncCallNode node) {
		// No nested standard functions are allowed. If parameter is used besides the
		// function call and this usage must be ignored, this is a problem, because in
		// the leave hook we remove the node ID from the ignore set.
		if (transformingStdFuncNames.contains(node.getFuncName())) {
			for (var e : node.getParams()) {
				ignore.add(e.getNodeID());
			}
		}
	}

	@Override
	public void leaveStdFuncCall(StdFuncCallNode node, RuntimeObject[] arguments) {
		if (transformingStdFuncNames.contains(node.getFuncName())) {
			for (var e : node.getParams()) {
				ignore.remove(e.getNodeID());
			}
		}

		// Work in progress: needed for math_expr.py, parseVariable so we
		// get constant names and function names as terminal symbol (instead of a
		// character sequence).
		if ("in".equals(node.getFuncName())) {
			// Find start index.
			int startIndex = Integer.MAX_VALUE;
			for (var e : arguments[0].getSymbolicObject().getNode()) {
				var slice = toSliceNode(e);
				if (slice != null) {
					int index = executeConst(slice.getBeginExpr()).intValue();
					startIndex = Math.min(index, startIndex);
				}
			}
			// Does not work for calc because character sequence is not removed before the
			// terminal symbol added here.
			// System.out.println("1> "+node);
			// ParseTree terminalSymbol = factory.createTerminal(arguments[0].stringValue(),
			// startIndex);
			// nodeStack.peek().addUniqueChild(terminalSymbol);
		}
	}

	public ParseTree getRoot() {
		return root;
	}

	protected boolean isLexerFunction() {
		return LEXER_FUNCTIONS.contains(functionStackTrace.peek());
	}

	private RuntimeObject executeConst(ExpressionNode constExpr) {
		Frame dummy = new Frame("dummy-execute-const", null);
		try {
			return constExpr.execute(dummy);
		} catch (InterpreterException e) {
			LOGGER.warning("Const execution error: " + e.getMessage());
			return TypeSystem.getDefault().newIntValue(0);
		}
	}

	private static ArrayAccessNode toArrayAccessNode(InterpreterNode node) {
		if (node instanceof ArrayAccessNode) {
			if (isInput(((ArrayAccessNode) node).getArrayName())) {
				return (ArrayAccessNode) node;
			}
		}
		return null;
	}

	private static SliceNode toSliceNode(InterpreterNode node) {
		if (node instanceof SliceNode) {
			if (isInput(((SliceNode) node).getSequenceName())) {
				return (SliceNode) node;
			}
		}
		return null;
	}

	private static boolean isInput(InterpreterNode node) {
		return node instanceof IdentifierStaticTypeNode;
	}

	/**
	 * Answers whether the node is potentially an access to the program input. This
	 * check is done on the AST. The actually check can be done on the (dynamically)
	 * constructed symbolic objects only.
	 *
	 * @param node the interpreter node to test
	 * @return {@code true} if the given node is potentially an access to the
	 *         program input; {@code false} otherwise
	 */
	private static boolean inputAccessIsPossible(InterpreterNode node) {
		return node instanceof ArrayAccessNode || node instanceof SliceNode;
	}

	/**
	 * Returns all nodes part of the tree with the root {@code tree}. <b>Use this
	 * method because {@see InterpreterNode#iterator()} does not handle the
	 * SelectNode correctly.</b>
	 *
	 * @param tree the tree root
	 * @return list of all nodes (including the root) of the tree
	 */
	private static List<InterpreterNode> nodesOf(InterpreterNode tree) {
		var result = new ArrayList<InterpreterNode>();
		for (var e : tree) {
			result.add(e);
			if (e instanceof SelectNode) {
				var select = (SelectNode) e;
				result.add(select.getArrayName());
				result.addAll(List.of(select.getSubscripts()));
			}
		}
		return result;
	}

}
