package at.fhooe.se.atgminer.atg;

public abstract class ThrowingATElementVisitor<T> extends AbstractATElementVisitor<T> {

	private final String message;

	public ThrowingATElementVisitor() {
		this("Visitor does not support elements of type %s");
	}

	public ThrowingATElementVisitor(String message) {
		this.message = message;
	}

	@Override
	public T visitGrammar(ATGrammar grammar) {
		throw new UnsupportedOperationException(String.format(message, "Grammar"));
	}

	@Override
	public T visitRule(ATRule rule) {
		throw new UnsupportedOperationException(String.format(message, "Rule"));
	}

	@Override
	public T visitTerminal(ATTerminal symbol) {
		throw new UnsupportedOperationException(String.format(message, "ATTerminal"));
	}

	@Override
	public T visitTerminalClass(ATTerminalClass symbol) {
		throw new UnsupportedOperationException(String.format(message, "ATTerminalClass"));
	}

	@Override
	public T visitNonTerminal(ATNonTerminal symbol) {
		throw new UnsupportedOperationException(String.format(message, "ATNonTerminal"));
	}

	@Override
	public T visitSequence(ATSequence element) {
		throw new UnsupportedOperationException(String.format(message, "ATSequence"));
	}

	@Override
	public T visitOption(ATOption element) {
		throw new UnsupportedOperationException(String.format(message, "ATOption"));
	}

	@Override
	public T visitRepetition(ATRepetition element) {
		throw new UnsupportedOperationException(String.format(message, "ATRepetition"));
	}

	@Override
	public T visitChoice(ATChoice element) {
		throw new UnsupportedOperationException(String.format(message, "ATChoice"));
	}

	@Override
	public T visitTerminalGroup(ATTerminalGroup group) {
		throw new UnsupportedOperationException(String.format(message, "ATTerminalGroup"));
	}
}
