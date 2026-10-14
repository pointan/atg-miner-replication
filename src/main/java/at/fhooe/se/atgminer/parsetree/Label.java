package at.fhooe.se.atgminer.parsetree;

import at.fhooe.se.codeanalysis.adele.interpreter.runtime.NoneType;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.RuntimeObject;

public class Label {

	public static final String WHILE = "while";
	public static final String IF = "if";
	public static final String CASE = "if";
	public static final String IF_TRUE_BRANCH = "1";
	public static final String IF_FALSE_BRANCH = "0";
	public static final String CASE_TRUE_BRANCH = "1";
	public static final String CASE_FALSE_BRANCH = "0";

	public static String ofWhileNode(ParseTree parent) {
		if (parent != null) {
			return parent.getLabel() + "_" + WHILE;
		} else {
			return WHILE;
		}
	}

	public static String ofIfNode(ParseTree parent) {
		if (parent != null) {
			return parent.getLabel() + "_" + Label.IF;
		} else {
			return Label.IF;
		}
	}

	public static String ofIfNode(ParseTree parent, boolean condition) {
		if (parent != null) {
			return parent.getLabel() + "_" + ofIfCondition(condition);
		} else {
			return ofIfCondition(condition);
		}
	}

	public static String ofIfNode(ParseTree parent, int nodeID) {
		if (parent != null) {
			return parent.getLabel() + "_" + Label.IF + nodeID;
		} else {
			return Label.IF + nodeID;
		}
	}

	public static String ofIfCondition(RuntimeObject condition) {
		if (condition.getValue() instanceof Boolean) {
			return ofIfCondition(condition.boolValue());
		}
		if (condition.getType() instanceof NoneType) {
			return ofIfCondition(false);
		}
		throw new IllegalStateException("Not yet supported condition type");
	}

	public static String ofIfCondition(boolean value) {
		return (value) ? Label.IF_TRUE_BRANCH : Label.IF_FALSE_BRANCH;
	}

	public static String ofCaseNode(ParseTree parent) {
		if (parent != null) {
			return parent.getLabel() + "_" + Label.CASE;
		} else {
			return Label.CASE;
		}
	}

	public static String ofCaseCondition(boolean value) {
		return (value) ? Label.CASE_TRUE_BRANCH : Label.CASE_FALSE_BRANCH;
	}
}
