package at.fhooe.se.atgminer.concept.v2.pdf;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Comparison {

	private final static Random RANDOM = new Random();

	public static final String EMPTY_LITERAL = "";

	private final int startIndex;
	private final int endIndex;
	private final List<String> literals;

	public Comparison(int startIndex, int endIndex, List<String> literals) {
		this.startIndex = Objects.checkIndex(startIndex, Integer.MAX_VALUE);
		this.endIndex = Objects.checkIndex(endIndex, Integer.MAX_VALUE);
		this.literals = Objects.requireNonNull(literals);
		if (literals.size() == 0) {
			throw new IllegalArgumentException("Literal list must not be empty");
		}
	}

	@Override
	public String toString() {
		return String.format("CMP[%d:%d] with %s", startIndex, endIndex, literals.toString());
	}

	public boolean startsAt(int index) {
		return index == startIndex;
	}

	public String getRandomLiteral() {
		if (literals.size() == 1) {
			return literals.get(0);
		} else {
			var ri = RANDOM.nextInt(literals.size());
			return literals.get(ri);
		}
	}

	public List<String> getLiterals() {
		return literals;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public boolean containsIndex(int index) {
		return startIndex <= index && index <= endIndex;
	}

}
