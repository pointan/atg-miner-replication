package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Semantic class, that is an abstraction for different know functions.</p>
 */
public abstract class FunctionSemantic extends BaseSemantic {
    public FunctionSemantic(InterpreterNode node) {
        super(node);
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitFunctionSemantic(this);
    }
}
