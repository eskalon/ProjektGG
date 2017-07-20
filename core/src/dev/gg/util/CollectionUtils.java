package dev.gg.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * This class contains utility methods for the work with collections.
 */
public class CollectionUtils {

	private CollectionUtils() {
	}

	/**
	 * Searches a map for the key to a given value.
	 * 
	 * @param map
	 *            The map.
	 * @param value
	 *            The value.
	 * @return The first key whose value matches the given one.
	 */
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
		for (Entry<T, E> entry : map.entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

}
