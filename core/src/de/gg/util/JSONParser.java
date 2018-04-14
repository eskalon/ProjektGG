package de.gg.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * A JSON Paser based on {@linkplain Gson Gson}.
 * 
 * @see #parseJson(String, Class)
 */
public class JSONParser {

	/**
	 * Gson-Parser.
	 */
	private static Gson gson = new GsonBuilder()
			.excludeFieldsWithModifiers(Modifier.STATIC)
			.setExclusionStrategies(
					new JSONParser().new ExcludeAnnotationExclusionStrategy())
			.setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	private JSONParser() {
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
	public static <T> T parseFromJson(String jsonInput, Class<T> clazz)
			throws JsonSyntaxException {
		return (T) gson.fromJson(jsonInput, clazz);
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
	public static <T> T parseFromJson(String jsonInput, Type type)
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
	public static String parseToJson(Object object) throws JsonSyntaxException {
		return gson.toJson(object);
	}

	/**
	 * Excludes fields marked with {@link ExcludeFromJSON} from the
	 * serialization.
	 */
	public class ExcludeAnnotationExclusionStrategy
			implements
				ExclusionStrategy {
		@Override
		public boolean shouldSkipClass(Class<?> arg0) {
			return false;
		}

		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			return f.getAnnotation(ExcludeFromJSON.class) != null;
		}
	}

	/**
	 * If a field is marked with this annotation it is excluded from the json
	 * serialization.
	 * 
	 * @see ExcludeAnnotationExclusionStrategy
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface ExcludeFromJSON {
	}
}
