package de.eskalon.gg.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import de.damios.guacamole.Preconditions;

/**
 * This class contains utility methods for the work with collections.
 */
public final class CollectionUtils {

	private CollectionUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Searches a map for the key to a given value.
	 *
	 * @param map
	 *            the map
	 * @param value
	 *            the value
	 * @return the first key whose value matches the given one; {@code null} if
	 *         the map doesn't contain this value.
	 */
	public static synchronized <T, E> T getKeyByValue(Map<T, E> map, E value) {
		Preconditions.checkNotNull(map, "The map cannot be null");

		for (Entry<T, E> entry : map.entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Sorts a map by its value.
	 *
	 * @param map
	 * @return the sorted map
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		Preconditions.checkNotNull(map, "The map cannot be null");

		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue((v1, v2) -> v2.compareTo(v1))); // biggest
																			// element
																			// first

		Map<K, V> result = new LinkedHashMap<>();
		for (Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	/**
	 * Returns the most popular element in a list.
	 *
	 * @param list
	 *            the list to look at
	 * @param T
	 *            the type of the list element; has to implement
	 *            {@link Comparable}
	 * @return the most popular value in the given list
	 */
	public static <T extends Comparable<T>> T findMostPopularElement(
			List<T> list) {
		Preconditions.checkNotNull(list, "The list cannot be null");

		Collections.sort(list);

		T previous = list.get(0);
		T popular = list.get(0);
		int count = 1;
		int maxCount = 1;

		for (int i = 1; i < list.size(); i++) {
			if (list.get(i) == previous)
				count++;
			else {
				if (count > maxCount) {
					popular = list.get(i - 1);
					maxCount = count;
				}
				previous = list.get(i);
				count = 1;
			}
		}

		return count > maxCount ? list.get(list.size() - 1) : popular;
	}

}
