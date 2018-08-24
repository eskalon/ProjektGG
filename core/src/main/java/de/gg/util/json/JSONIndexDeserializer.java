package de.gg.util.json;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

/**
 * Deserializes an JSON primitive by interpreting it as the index of the given
 * values list.
 */
public class JSONIndexDeserializer<T> implements JsonDeserializer<T> {

	private final List<T> values;

	public JSONIndexDeserializer(List<T> values) {
		this.values = values;
	}

	@Override
	public T deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonPrimitive prim = json.getAsJsonPrimitive();
		if (prim.getAsInt() == -1 || prim.getAsInt() > values.size() - 1)
			return null;
		return values.get(prim.getAsInt());
	}

}
