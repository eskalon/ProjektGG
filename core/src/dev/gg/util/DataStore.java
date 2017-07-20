package dev.gg.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores data via a key.
 * 
 * @see #data
 */
public class DataStore {

	protected Map<String, Object> data = new ConcurrentHashMap<>();

	/**
	 * Associates the specified data with the specified key in this store. If
	 * the data store previously contained a mapping for the key, the old data
	 * is replaced by the specified data.
	 * 
	 * @param key
	 *            The key.
	 * @param data
	 *            The data.
	 */
	public void put(String key, Object data) {
		if (key == null) {
			throw new NullPointerException("Key cannot be null.");
		}

		if (key.isEmpty()) {
			throw new IllegalArgumentException("Key cannot be empty.");
		}

		if (data == null) {
			throw new NullPointerException("Data cannot be null.");
		}

		this.data.put(key, data);
	}

	/**
	 * 
	 * Returns true if this data store contains a mapping for the specified key.
	 * 
	 * @param key
	 *            The key of the value
	 * @return true if data store contains mapping for specified tag
	 */
	public boolean contains(String key) {
		return this.data.containsKey(key);
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this
	 * data store contains no mapping for the key.
	 * 
	 * @param key
	 *            The key.
	 * @return The value or null if the key wasn't added before.
	 */
	public Object get(String key) {
		return this.data.get(key);
	}

	/**
	 * Returns the value to which the specified key is mapped already casted to
	 * the specified class, or null if this data store contains no mapping for
	 * the key.
	 * 
	 * @param key
	 *            The key.
	 * @param clazz
	 *            The class to which the data has to be casted.
	 * @return The value or null if the key wasn't added before.
	 */
	public <T> T get(String key, Class<T> clazz) {
		Object obj = this.get(key);

		if (obj == null) {
			return null;
		}

		return clazz.cast(obj);
	}
}
