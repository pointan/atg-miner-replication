package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.BlockSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.BlockStmtNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Converts a {@link BlockStmtNode} into a {@link BlockSemantic}</p>
 */
public class BlockStmtNodeToBlockSemantic implements NodeToSemantic<BlockStmtNode, BlockSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> statementProcessor;

    public BlockStmtNodeToBlockSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> statementProcessor) {
        this.statementProcessor = statementProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof BlockStmtNode;
    }

    @Override
    public BlockSemantic process(BlockStmtNode node) {
        BlockSemantic blockSemantic = new BlockSemantic(node);
        node.getStatements().stream().map(statementProcessor::process).forEach(blockSemantic::addSemantic);
        return blockSemantic;
    }
}
