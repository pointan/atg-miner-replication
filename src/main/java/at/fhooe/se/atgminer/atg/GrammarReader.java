package at.fhooe.se.atgminer.atg;

import java.io.InputStream;

/**
 * <p>Reads the grammar from a given input stream</p>
 */
public interface GrammarReader {
	/**
	 * Loads the grammar from a given input stream.
	 * @param input the input from where to load the grammar.
	 * @return the resulting grammar
	 */
	ATGrammar readGrammar(InputStream input);
}
