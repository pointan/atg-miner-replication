package at.fhooe.se.atgminer.util;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>Util class to deal with Strings.</p>
 */
public class StringUtil {
	private StringUtil() {}

	public static Map<String, Integer> findRepeatingMatches(String str) {
		Pattern pattern = Pattern.compile("(\\b.+?\\b)\\1+");
		Matcher matcher = pattern.matcher(str);
		Map<String, Integer> result = new HashMap<>();
		while(matcher.find()) {
			String match = matcher.group(1);
			String group = matcher.group(0);
			result.put(match, group.length() / match.length());
		}
		return result;
	}

	public static String replaceMultiOccurrencesWithString(String str, String search, String replace) {
		Pattern pattern = Pattern.compile("("+Pattern.quote(search)+")\\1*");
		Matcher matcher = pattern.matcher(str);
		while(matcher.find()) {
			str = matcher.replaceAll(replace);
		}
		return str;
	}

	public static OptionalInt tryParseInt(String string) {
		try {
			return OptionalInt.of(Integer.parseInt(string));
		} catch (NumberFormatException e) {
			return OptionalInt.empty();
		}
	}

	public static boolean biDirectStartsWith(String s1, String s2) {
		if (s1 == null || s2 == null) return false;
		return s1.startsWith(s2) || s2.startsWith(s1);
	}

	public static String upperCaseFirst(String str) {
		if (str.length() > 0) {
			char[] c = str.toCharArray();
			c[0] = Character.toUpperCase(c[0]);
			return new String(c);
		} else {
			return str;
		}
	}

	public static boolean doesPenultimateCharacterMatchesX(String value, char toCheck) {
		if (value.length() == 1) return false;
		return value.charAt(value.length() - 2) == toCheck;
	}

	public static boolean containsInEitherIgnoreCase(String str1, String str2) {
		str1 = str1.toLowerCase();
		str2 = str2.toLowerCase();
		return str1.contains(str2) || str2.contains(str1);
	}

	public static String ident(String identChar, int identTimes) {
		return IntStream.range(0, identTimes).mapToObj(x -> identChar).collect(Collectors.joining());
	}
}
