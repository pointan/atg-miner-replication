package at.fhooe.se.atgminer.util;

/**
 * <p>Utility class to cast elements</p>
 */
public class CastUtil {
	private CastUtil() {

	}

	/**
	 * Cast any object to a given type
	 *
	 * @param o   the object to be casted
	 * @param <T> the type to which it should be casted
	 * @return the casted element
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object o) {
		return (T) o;
	}
}
