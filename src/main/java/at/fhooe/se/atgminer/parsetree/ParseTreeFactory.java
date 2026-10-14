package at.fhooe.se.atgminer.parsetree;

public class ParseTreeFactory {
	private int nodeCounter;

	private String nextId() {
		nodeCounter++;
		return "node" + nodeCounter;
	}

	public ParseTree createNonTerminal(String name) {
		ParseTree result = new ParseTree(nextId(), name);
		result.setIsTerminal(false);
		return result;
	}

	public ParseTree createTerminal(String name) {
		ParseTree result = new ParseTree(nextId(), name);
		result.setIsTerminal(true);
		result.setRemovable(false);
		return result;
	}

	public ParseTree createTerminal(String name, int inputStartIndex) {
		ParseTree result = new ParseTree(nextId(), name, inputStartIndex);
		result.setIsTerminal(true);
		result.setRemovable(false);
		return result;
	}

	public ParseTree createTerminalClass(String name, String value, int inputStartIndex) {
		ParseTree result = new ParseTree(nextId(), name, inputStartIndex);
		result.setTerminalClassAttributeValue(value);
		result.setIsTerminalClass(true);
		result.setRemovable(false);
		return result;
	}

}
