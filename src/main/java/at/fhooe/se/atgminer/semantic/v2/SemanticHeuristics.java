package at.fhooe.se.atgminer.semantic.v2;

import at.fhooe.se.atgminer.atg.semantics.*;

import java.util.List;
import java.util.Map;

/**
 * <p>Class that is used for some semantic heuristics</p>
 */
public class SemanticHeuristics {

    private static final List<BaseSemantic> INPUT_ACCESS_REPLACEMENT_SEMANTIC = List.of(createSelfLineIndexSemantic());

    /**
     * Create the access to self.line[self.idx]
     *
     * @return the base semantic for accessing the line property at the index position
     */
    public static BaseSemantic createSelfLineIndexSemantic() {
        return new ArrayAccessSemantic(
                null,
                createQualifiedIdentifierSemantic("self", "line"),
                new BaseSemantic[]{createQualifiedIdentifierSemantic("self", "idx")}
        );
    }

    /**
     * Replaces a heuristic inside an expression with a constant value
     *
     * @param expression the expression in which it should be replaced
     * @param heuristic  the heuristic to be searched for
     * @param val        the constant value that should be used instead
     * @return the resulting replaced expression
     */
    public static BaseSemantic replaceTerminalByHeuristic(BaseSemantic expression, BaseSemantic heuristic, Object val) {
        return expression.replace(Map.of(heuristic, new TerminalHeuristicPlaceHolder(null,new ConstSemantic(null,val))));
    }

    public static BaseSemantic inputAccessReplacementSemantic(BaseSemantic expression, Object val) {
        for (BaseSemantic baseSemantic : INPUT_ACCESS_REPLACEMENT_SEMANTIC) {
            expression = replaceTerminalByHeuristic(expression, baseSemantic, val);
        }
        return expression;
    }

    private static QualifiedIdentifierSemantic createQualifiedIdentifierSemantic(String qualifier, String member) {
        return new QualifiedIdentifierSemantic(
                null,
                new VariableSemantic(null,qualifier),
                new VariableSemantic(null,member)
        );
    }
}
