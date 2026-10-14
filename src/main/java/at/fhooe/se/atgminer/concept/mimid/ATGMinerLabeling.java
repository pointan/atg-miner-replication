package at.fhooe.se.atgminer.concept.mimid;

import at.fhooe.se.atgminer.parsetree.Label;
import at.fhooe.se.atgminer.parsetree.ParseTree;
import at.fhooe.se.codeanalysis.adele.interpreter.node.IfStmtNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.WhileStmtNode;

public class ATGMinerLabeling extends Labeling {

	public static final String IF = Label.IF;
	public static final String WHILE = Label.WHILE;

	private static final String IF_KEY = "_" + Label.IF + "_";

	public static boolean isSingleChildIfNode(ParseTree node) {
		if (node.getChildren().size() == 1) {
			return node.getChildren().get(0).getLabel().contains(IF_KEY);
		}
		else {
			return false;
		}
	}

	public ATGMinerLabeling() {
	}

	public String labelOfIfNode(ParseTree parent, IfStmtNode node) {
		if (parent != null) {
			return String.format("%s_%s_%d:", parent.getLabel(), IF, node.getNodeID());
		} else {
			return String.format("%s_%d:", IF, node.getNodeID());
		}
	}

	public String labelOfWhileNode(ParseTree parent, WhileStmtNode node) {
		if (parent != null) {
			return String.format("%s_%d_%s", parent.getLabel(), node.getNodeID(), WHILE);
		} else {
			return String.format("%d_%d:", node.getNodeID(), WHILE);
		}
	}

}
