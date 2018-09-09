package de.gg.utils.asset;

import java.lang.reflect.Type;

/**
 * A simple asset type for JSON files.
 *
 * @see JSONLoader The respective asset loader.
 */
public class JSON {

	private Object data;

	public JSON(Object data) {
		this.data = data;
	}

	@SuppressWarnings("unchecked")
	public <T> T getData(Class<T> clazz) {
		return (T) this.data;
	}

	@SuppressWarnings("unchecked")
	public <T> T getData(Type type) {
		return (T) this.data;
	}

}