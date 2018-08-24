package de.gg.util.json;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import de.gg.util.json.ExcludeAnnotationExclusionStrategy.ExcludeFromJSON;

/**
 * A simple JSON parser based on {@linkplain Gson Gson}.
 * <p>
 * Use the {@link ExcludeFromJSON} annotation to exclude classes and fields from
 * serialization.
 *
 * @see #parseJson(String, Class)
 */
public class SimpleJSONParser {

	/**
	 * Gson-Parser.
	 */
	private final Gson gson;

	protected SimpleJSONParser(Gson gson) {
		this.gson = gson;
	}

	protected SimpleJSONParser(GsonBuilder gsonBuilder) {
		this(gsonBuilder.excludeFieldsWithModifiers(Modifier.STATIC)
				.setExclusionStrategies(
						new ExcludeAnnotationExclusionStrategy())
				.setDateFormat("yyyy-MM-dd HH:mm:ss").create());
	}

	public SimpleJSONParser() {
		this(new GsonBuilder());
	}

	/**
	 * Parses the JSON input to the given Java class.
	 *
	 * @param <T>
	 *            Return Type.
	 * @param jsonInput
	 *            The JSON input as string.
	 * @param clazz
	 *            The class of the return type.
	 * @return The parsed object.
	 * @throws JsonSyntaxException
	 *             if there is a problem processing the JSON elements.
	 */
	public <T> T parseFromJson(String jsonInput, Class<T> clazz)
			throws JsonSyntaxException {
		return gson.fromJson(jsonInput, clazz);
	}

	/**
	 * Parses the JSON input to the given Java type.
	 *
	 * @param jsonInput
	 *            The JSON input as string.
	 * @param clazz
	 *            The return type.
	 * @return The parsed object.
	 * @throws JsonSyntaxException
	 *             if there is a problem processing the JSON elements.
	 */
	@SuppressWarnings("unchecked")
	public <T> T parseFromJson(String jsonInput, Type type)
			throws JsonSyntaxException {
		return (T) gson.fromJson(jsonInput, type);
	}

	/**
	 * Parses the Java input to the a JSON string.
	 *
	 * @param object
	 *            The java object as input.
	 * @return The parsed string.
	 * @throws JsonSyntaxException
	 *             if there is a problem processing the JSON elements.
	 */
	public String parseToJson(Object object) throws JsonSyntaxException {
		return gson.toJson(object);
	}

}
