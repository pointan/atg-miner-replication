package at.fhooe.se.atgminer.util;

import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * <p>
 * Pair class to store two values.
 * </p>
 */
public class Pair<S, T> {
	private final S first;
	private final T second;

	public Pair(S first, T second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public String toString() {
		return String.format("(%s,%s)", Objects.toString(first), Objects.toString(second));
	}

	public S getFirst() {
		return first;
	}

	public T getSecond() {
		return second;
	}

	public static <S, T> Collector<Pair<S, T>, ?, Map<S, T>> toMap() {
		return Collectors.toMap(Pair::getFirst, Pair::getSecond);
	}

	public static <S, T> Collector<Pair<S, T>, ?, Map<S, T>> toMap(BinaryOperator<T> mergeFunction) {
		return Collectors.toMap(Pair::getFirst, Pair::getSecond, mergeFunction);
	}

	public static <S, T> Pair<S, T> of(S first, T second) {
		return new Pair<>(first, second);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pair<?, ?> pair = (Pair<?, ?>) o;
		return Objects.equals(getFirst(), pair.getFirst()) && Objects.equals(getSecond(), pair.getSecond());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFirst(), getSecond());
	}
}
