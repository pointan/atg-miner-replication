package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.PrintSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.python.PrintNode;

public class PrintNodeToPrintSemantic implements NodeToSemantic<PrintNode, PrintSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor;

    public PrintNodeToPrintSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> operandProcessor) {
        this.operandProcessor = operandProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof PrintNode;
    }

    @Override
    public PrintSemantic process(PrintNode node) {
        return new PrintSemantic(node, operandProcessor.process(node.getParam(0)));
    }
}
