package at.fhooe.se.atgminer.atg.node2semantics.impl;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.FormalParamDefSemantic;
import at.fhooe.se.atgminer.atg.semantics.MethodDefSemantic;
import at.fhooe.se.codeanalysis.adele.interpreter.node.FormalParamDefNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.MethodDefNode;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Transforms a {@link MethodDefNode} into a {@link MethodDefSemantic}</p>
 */
public class MethodDefNodeToMethodDefSemantic implements NodeToSemantic<MethodDefNode, MethodDefSemantic> {

    private final NodeToSemantic<InterpreterNode, ? extends BaseSemantic> childProcessor;

    public MethodDefNodeToMethodDefSemantic(NodeToSemantic<InterpreterNode, ? extends BaseSemantic> childProcessor) {
        this.childProcessor = childProcessor;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return node instanceof MethodDefNode;
    }

    @Override
    public MethodDefSemantic process(MethodDefNode node) {
        return new MethodDefSemantic(
                node,
                node.getClassName(),
                node.getMethodName(),
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
