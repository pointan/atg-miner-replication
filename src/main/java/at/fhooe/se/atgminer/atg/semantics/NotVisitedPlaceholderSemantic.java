package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

/**
 * <p>Just an empty semantic, that indicates, that this path has not been visited.</p>
 */
public class NotVisitedPlaceholderSemantic extends EmptySemantic {

    public NotVisitedPlaceholderSemantic(InterpreterNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "???";
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitNotVisitedPlaceholderSemantic(this);
    }
}
