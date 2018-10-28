package de.gg.engine.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Preconditions;

/**
 * Stores data via a key. Basically a {@linkplain HashMap}.
 * 
 * @see #data
 */
public class DataStore {

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
	 *            The key.
	 * @param data
	 *            The data.
	 */
	public void put(String key, Object data) {
		Preconditions.checkNotNull(key);
		Preconditions.checkArgument(!key.isEmpty());
		Preconditions.checkNotNull(data);

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
		Preconditions.checkNotNull(key);
		Preconditions.checkArgument(!key.isEmpty());

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
		Preconditions.checkNotNull(key);
		Preconditions.checkArgument(!key.isEmpty());

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
		Preconditions.checkNotNull(key);
		Preconditions.checkArgument(!key.isEmpty());

		Object obj = this.get(key);

		if (obj == null) {
			return null;
		}

		return clazz.cast(obj);
	}
}
