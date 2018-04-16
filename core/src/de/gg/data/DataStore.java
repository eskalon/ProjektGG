package de.gg.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Preconditions;

/**
 * Stores data via a key. Basically a {@linkplain ConcurrentHashMap HashMap}.
 * 
 * @see #data
 */
public class DataStore {

	private static final String KEY_CANNOT_BE_NULL_MSG = "Key cannot be null.";
	private static final String KEY_CANNOT_BE_EMPTY_MSG = "Key cannot be empty.";
	private static final String DATA_CANNOT_BE_NULL_MSG = "Data cannot be null.";

	/**
	 * The hashmap containing the data.
	 */
	protected Map<String, Object> data = new ConcurrentHashMap<>();

	/**
	 * Associates the specified data with the specified key in this store. If
	 * the data store previously contained a mapping for the key, the old data
	 * is replaced by the specified data.
	 * 
	 * @param key
	 *            the key.
	 * @param data
	 *            the data.
	 */
	public void put(String key, Object data) {
		Preconditions.checkNotNull(key, KEY_CANNOT_BE_NULL_MSG);
		Preconditions.checkArgument(key.isEmpty(), KEY_CANNOT_BE_EMPTY_MSG);
		Preconditions.checkNotNull(data, DATA_CANNOT_BE_NULL_MSG);

		this.data.put(key, data);
	}

	/**
	 * Removes the key (and its corresponding value) from this map.
	 * 
	 * @param key
	 *            the key to remove.
	 */
	public void remove(String key) {
		Preconditions.checkNotNull(key, KEY_CANNOT_BE_NULL_MSG);
		Preconditions.checkArgument(key.isEmpty(), KEY_CANNOT_BE_EMPTY_MSG);

		this.data.remove(key);
	}

	/**
	 * 
	 * Returns true if this data store contains a mapping for the specified key.
	 * 
	 * @param key
	 *            the key of the value.
	 * @return <code>true</code> if the data store contains a mapping for the
	 *         specified tag.
	 */
	public boolean contains(String key) {
		Preconditions.checkNotNull(key, KEY_CANNOT_BE_NULL_MSG);
		Preconditions.checkArgument(key.isEmpty(), KEY_CANNOT_BE_EMPTY_MSG);

		return this.data.containsKey(key);
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this
	 * data store contains no mapping for the key.
	 * 
	 * @param key
	 *            the key.
	 * @return the value or <code>null</code> if the key wasn't added before.
	 */
	public Object get(String key) {
		Preconditions.checkNotNull(key, KEY_CANNOT_BE_NULL_MSG);
		Preconditions.checkArgument(key.isEmpty(), KEY_CANNOT_BE_EMPTY_MSG);

		return this.data.get(key);
	}

	/**
	 * Returns the value to which the specified key is mapped already casted to
	 * the specified class, or null if this data store contains no mapping for
	 * the key.
	 * 
	 * @param key
	 *            the key.
	 * @param clazz
	 *            the class to which the data has to be casted.
	 * @return the value or <code>null</code> if the key wasn't added before.
	 */
	public <T> T get(String key, Class<T> clazz) {
		Preconditions.checkNotNull(key, KEY_CANNOT_BE_NULL_MSG);
		Preconditions.checkArgument(key.isEmpty(), KEY_CANNOT_BE_EMPTY_MSG);

		Object obj = this.get(key);

		if (obj == null) {
			return null;
		}

		return clazz.cast(obj);
	}
}
