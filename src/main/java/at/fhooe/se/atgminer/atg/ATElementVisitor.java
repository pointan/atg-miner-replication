package at.fhooe.se.atgminer.atg;

public interface ATElementVisitor<T> {

    T visitGrammar(ATGrammar grammar);

    T visitRule(ATRule rule);

    T visitTerminal(ATTerminal symbol);

    T visitTerminalClass(ATTerminalClass symbol);

    T visitTerminalGroup(ATTerminalGroup group);

    T visitNonTerminal(ATNonTerminal symbol);

    T visitSequence(ATSequence element);

    T visitOption(ATOption element);

    T visitRepetition(ATRepetition element);

    T visitChoice(ATChoice element);
}
