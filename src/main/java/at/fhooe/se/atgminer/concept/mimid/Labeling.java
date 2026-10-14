package at.fhooe.se.atgminer.concept.mimid;

import at.fhooe.se.atgminer.parsetree.ParseTree;
import at.fhooe.se.codeanalysis.adele.interpreter.node.IfStmtNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.WhileStmtNode;

/**
 * Abstraction for labelling pseudo method nodes that are created from
 * conditional statements and loop statements. ATG Miner and Mimid implement
 * different strategies. This abstraction allows flexibility in using labelling
 * strategies.
 */
public abstract class Labeling {

	public abstract String labelOfIfNode(ParseTree parent, IfStmtNode node);

	public abstract String labelOfWhileNode(ParseTree parent, WhileStmtNode node);

}
