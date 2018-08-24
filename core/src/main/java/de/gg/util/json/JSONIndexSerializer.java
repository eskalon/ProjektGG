package de.gg.util.json;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serializes an object as the respective index of the given values list.
 */
public class JSONIndexSerializer<T> implements JsonSerializer<T> {

	private final List<T> values;

	public JSONIndexSerializer(List<T> values) {
		this.values = values;
	}

	@Override
	public JsonElement serialize(T src, Type typeOfSrc,
			JsonSerializationContext context) {
		for (int i = 0; i < values.size(); i++)
			if (values.get(i) == src)
				return new JsonPrimitive(i);
		return new JsonPrimitive(-1);
	}

}
