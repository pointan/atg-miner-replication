package at.fhooe.se.atgminer.atg.coco;

import java.io.InputStream;

import at.fhooe.se.atgminer.atg.ATGrammar;
import at.fhooe.se.atgminer.atg.GrammarReader;
import at.fhooe.se.atgminer.concept.v2.GrammarRenderer;

/**
 * A parser for the Coco/R input language.
 */
public class CocoGrammarParser implements GrammarReader {

	public CocoGrammarParser() {
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: ");
			System.out.println("  java CocoGrammarParser <grammar file>");
			return;
		}
		var atg = new CocoGrammarParser().parse(args[0]);
		System.out.println(GrammarRenderer.getCocoRRenderer().render(atg));
	}

	@Override
	public ATGrammar readGrammar(InputStream input) {
		return parse(input);
	}

	public ATGrammar parse(InputStream input) {
		var s = new Scanner(input);
		var p = new Parser(s);
		p.Parse();
		return p.grammar;
	}

	public ATGrammar parse(String fileName) {
		var s = new Scanner(fileName);
		var p = new Parser(s);
		p.Parse();
		return p.grammar;
	}
}
