package at.fhooe.se.atgminer.atg;

public abstract class AbstractATElementVisitor<T> implements ATElementVisitor<T> {

    public T visitGrammar(ATGrammar grammar) {
        return null;
    }

    public T visitRule(ATRule rule) {
        return null;
    }

    public T visitTerminal(ATTerminal symbol) {
        return null;
    }

    public T visitTerminalClass(ATTerminalClass symbol) {
        return null;
    }

    public T visitTerminalGroup(ATTerminalGroup group) {
        return null;
    }

    public T visitNonTerminal(ATNonTerminal symbol) {
        return null;
    }

    public T visitSequence(ATSequence element) {
        return null;
    }

    public T visitOption(ATOption element) {
        return null;
    }

    public T visitRepetition(ATRepetition element) {
        return null;
    }

    public T visitChoice(ATChoice element) {
        return null;
    }
}
