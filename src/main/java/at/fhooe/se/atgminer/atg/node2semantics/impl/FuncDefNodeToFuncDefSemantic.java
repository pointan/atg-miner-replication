package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.FormalParamDefSemantic;
import at.fhooe.se.atgminer.atg.semantics.FuncDefSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.FormalParamDefNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.FuncDefNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.MethodDefNode;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Transforms a {@link FuncDefNode} to a {@link FuncDefSemantic}</p>
 */
public class FuncDefNodeToFuncDefSemantic implements NodeToSemantic<FuncDefNode, FuncDefSemantic> {
    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> childProcessor;

    public FuncDefNodeToFuncDefSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> childProcessor) {
        this.childProcessor = childProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof FuncDefNode;
    }

    @Override
    public FuncDefSemantic process(FuncDefNode node) {
        return new FuncDefSemantic(
                node,
                node.getFuncName(),
                node.getBody().stream().map(childProcessor::process).collect(Collectors.toList()),
                Stream.of(node.getFormalParameters()).map(this::process).collect(Collectors.toList())
        );
    }

    private FormalParamDefSemantic process(FormalParamDefNode node) {
        return new FormalParamDefSemantic(
                node,
                node.getIdent()
        );
    }
}
