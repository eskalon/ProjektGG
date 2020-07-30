package de.gg.game.asset;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * A simple JSON parser based on {@linkplain Gson Gson}.
 * 
 * @author damios
 */
public class SimpleJSONParser {

	/**
	 * Gson-Parser.
	 */
	private final Gson gson;

	protected SimpleJSONParser(Gson gson) {
		this.gson = gson;
	}

	/**
	 * Creates a simple JSON parser with the following features:
	 * <p>
	 * <ul>
	 * <li>static fields are automatically excluded</li>
	 * <li>use the {@link ExcludeFromJSON} annotation to exclude additional
	 * classes and fields from the serialization</li>
	 * <li>the date format used is {@code yyyy-MM-dd HH:mm:ss}</li>
	 * </ul>
	 * 
	 * @param gsonBuilder
	 */
	protected SimpleJSONParser(GsonBuilder gsonBuilder) {
		this(gsonBuilder.excludeFieldsWithModifiers(Modifier.STATIC)
				.setExclusionStrategies(
						new ExcludeAnnotationExclusionStrategy())
				.setDateFormat("yyyy-MM-dd HH:mm:ss").create());
	}

	/**
	 * @see #SimpleJSONParser(GsonBuilder)
	 */
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
	 * @param <T>
	 *            Return Type.
	 * 
	 * @param jsonInput
	 *            The JSON input as string.
	 * @param type
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
