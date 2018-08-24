package de.gg.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;

import com.google.common.base.Preconditions;

/**
 * This class contains utility methods for the work with collections.
 */
public class CollectionUtils {

	private CollectionUtils() {
		// not used
	}

	/**
	 * Searches a map for the key to a given value.
	 *
	 * @param map
	 *            The map.
	 * @param value
	 *            The value.
	 * @return The first key whose value matches the given one.
	 *         <Code>Null</code> if the map doesn't contain this value.
	 */
	public static synchronized <T, E> T getKeyByValue(Map<T, E> map, E value) {
		for (Entry<T, E> entry : map.entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Sorts a hashmap by its value.
	 *
	 * @param map
	 * @return the sorted hashmap.
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue());

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
	 *            The list to look at.
	 * @param T
	 *            The type of the list element. Has to implement
	 *            {@link Comparable}.
	 * @return the most popular value in the given list.
	 */
	public static <T extends Comparable> T findMostPopularElement(
			List<T> list) {
		Preconditions.checkNotNull(list);

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

	/**
	 * Returns a random element from the given list.
	 *
	 * @param list
	 * @param random
	 * @return
	 */
	public static <T> T getRandomElementInList(List<T> list, Random random) {
		int index = random.nextInt(list.size());
		T item = list.get(index);
		return item;
	}

}
