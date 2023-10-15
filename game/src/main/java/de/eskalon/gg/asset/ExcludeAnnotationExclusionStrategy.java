package de.eskalon.gg.asset;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Excludes fields marked with {@link ExcludeFromJSON} from the
 * {@linkplain SimpleJSONParser serialization}.
 * 
 * @author damios
 */
public class ExcludeAnnotationExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipClass(Class<?> arg0) {
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return f.getAnnotation(ExcludeFromJSON.class) != null;
	}

	/**
	 * If a field is marked with this annotation it is excluded from the
	 * {@linkplain SimpleJSONParser JSON serialization}.
	 *
	 * @see ExcludeAnnotationExclusionStrategy
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public @interface ExcludeFromJSON {
	}

}
