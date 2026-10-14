package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Class that represents the access to a member of an object.</p>
 */
public class QualifiedIdentifierSemantic extends BaseSemantic {
    private final BaseSemantic qualifiers;
    private final BaseSemantic member;

    public QualifiedIdentifierSemantic(InterpreterNode node, BaseSemantic qualifiers, BaseSemantic member) {
        super(node);
        this.qualifiers = qualifiers;
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QualifiedIdentifierSemantic that = (QualifiedIdentifierSemantic) o;
        return Objects.equals(qualifiers, that.qualifiers) && Objects.equals(member, that.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiers, member);
    }

    @Override
    public String toString() {
        return qualifiers + "." + member;
    }

    @Override
    public List<BaseSemantic> getChildren() {
        return List.of(qualifiers, member);
    }

    @Override
    public BaseSemantic replace(Map<? extends BaseSemantic, ? extends BaseSemantic> replacement) {
        if (replacement.containsKey(this)) {
            return replacement.get(this);
        }
        return new QualifiedIdentifierSemantic(getBasedOn(), qualifiers.replace(replacement), member.replace(replacement));
    }

    @Override
    public <T> T accept(SemanticVisitor<T> visitor) {
        return visitor.visitQualifiedIdentifierSemantic(this);
    }

    public BaseSemantic getQualifiers() {
        return qualifiers;
    }

    public BaseSemantic getMember() {
        return member;
    }
}
