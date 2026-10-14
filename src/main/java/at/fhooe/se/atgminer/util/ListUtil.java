package at.fhooe.se.atgminer.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * <p>Utility class for {@link java.util.List}s</p>
 */
public class ListUtil {
    private ListUtil() {
    }

    /**
     * returns the last element from a list
     *
     * @param list the list where to search the last element from
     * @param <T>  the type of the elements in the list
     * @return the last element of the list if present
     * @throws IllegalStateException if the list is empty
     */
    public static <T> T getLastElementFromList(List<T> list) {
        if (list.isEmpty()) throw new IllegalStateException("Cannot access last element from empty list");
        return list.get(list.size() - 1);
    }

    /**
     * returns the next element in the list given a list and an element
     *
     * @param list    the list
     * @param element the element for which the next element should be searched for
     * @param <T>     the type of the elements in the list
     * @return an optional containing the next element, if one exists
     */
    public static <T> Optional<T> nextElementInList(List<T> list, T element) {
        int indexOf = list.indexOf(element);
        if (indexOf + 1 >= list.size() || indexOf == -1) return Optional.empty();
        return Optional.of(list.get(indexOf + 1));
    }

    /**
     * check if all lists inside the list are equal
     *
     * @param listOfLists the list of lists that should be checked
     * @param <T>         the type of the elements in the list
     * @return true if all lists are equal
     */
    public static <T> boolean areEqual(List<List<T>> listOfLists) {
        if (listOfLists.isEmpty()) return true;
        List<T> last = listOfLists.get(0);
        for (int i = 1; i < listOfLists.size(); i++) {
            List<T> current = listOfLists.get(i);
            if (!current.equals(last)) return false;
            last = current;
        }
        return true;
    }

    /**
     * Returns a new list with all elements that are in list "a" but not in list "b"
     *
     * @param a   the first list
     * @param b   the second list
     * @param <T> the type of the lists
     * @return a new list x that is a - b
     */
    public static <T> List<T> subtract(List<T> a, List<T> b) {
        List<T> res = new ArrayList<>(a);
        res.removeAll(b);
        return res;
    }

    /**
     * Concatenates two list
     *
     * @param list1 the first list
     * @param list2 the second list
     * @param <T>   the type of the lists
     * @return the combination of both lists
     */
    public static <T> List<T> concat(List<T> list1, List<T> list2) {
        List<T> res = new ArrayList<>(list1);
        res.addAll(list2);
        return res;
    }

    /**
     * Try to get a specific index of the list, if the index is out of range it returns an empty element.
     *
     * @param list  the list
     * @param index the index to get
     * @param <T>   the type of the elements
     * @return the resulting element or an empty optional
     */
    public static <T> Optional<T> tryGetIndex(List<T> list, int index) {
        if (index >= list.size() || index < 0) return Optional.empty();
        return Optional.of(list.get(index));
    }

    /**
     * Replaces a specific element in a list
     *
     * @param list          the list
     * @param elemToReplace the element to replace
     * @param replacement   the new element
     * @param <T>           the type of the elements
     */
    public static <T> void replace(List<T> list, T elemToReplace, T replacement) {
        list.set(list.indexOf(elemToReplace), replacement);
    }

    /**
     * Returns a subList of the given list between beginExcl+1 and endExcl-1
     *
     * @param list      the list where the sublist should be extracted from
     * @param beginExcl the start index if it is smaller than 0 the list will start with the first element
     * @param endExcl   the end index. if it is smaller than 0 or greater than max(list) the list will be not bounded at
     *                  the end
     * @param <T>       the type of the elements
     * @return the sub list
     */
    public static <T> List<T> subListExclusive(List<T> list, int beginExcl, int endExcl) {
        int maxIdx = list.size();
        if (beginExcl > maxIdx) {
            return List.of();
        }

        if (endExcl > maxIdx || endExcl < 0) {
            endExcl = maxIdx;
        }
        if (beginExcl < 0) {
            beginExcl = 0;
        } else {
            beginExcl++;
        }

        return list.subList(beginExcl, endExcl);
    }

    public static <T> void moveLists(List<T> from, List<T> to, int idx) {
        to.addAll(idx,from);
        from.clear();
    }

    public static <T> void moveLists(List<T> from, List<T> to) {
        to.addAll(from);
        from.clear();
    }
}
