package de.gg.engine.asset;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;

import javax.annotation.Nullable;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class AnnotationAssetManager extends AssetManager {

	private HashMap<Class<?>, AssetLoaderParametersFactory<?>> paramFactories = new HashMap<>();

	public AnnotationAssetManager(FileHandleResolver resolver) {
		super(resolver);
	}

	/**
	 * @param clazz
	 *            the class whose fields annotated with {@link InjectAsset
	 *            Asset} should get loaded
	 */
	@SuppressWarnings("unchecked")
	public <T> void load(Class<T> clazz) {
		for (Field field : ClassReflection.getDeclaredFields(clazz)) {
			if (!field.isAnnotationPresent(InjectAsset.class))
				continue;
			InjectAsset asset = field.getDeclaredAnnotation(InjectAsset.class)
					.getAnnotation(InjectAsset.class);
			if (!asset.disabled())
				load(asset.value(), field.getType(),
						getAssetLoaderParameters(asset, field));
		}

		if (clazz.getSuperclass() != null) {
			load(clazz.getSuperclass());
		}
	}

	/**
	 * Sets the fields of the given instance (or only the static fields if the
	 * instance is <code>null</code>) to the previously {@linkplain #load(Class)
	 * loaded} assets.
	 * 
	 * @param clazz
	 * @param instance
	 */
	private <T> void injectAssets(Class<T> clazz, @Nullable T instance) {
		for (Field field : ClassReflection.getDeclaredFields(clazz)) {
			if (!field.isAnnotationPresent(InjectAsset.class))
				continue;
			InjectAsset asset = field.getDeclaredAnnotation(InjectAsset.class)
					.getAnnotation(InjectAsset.class);

			if (!asset.disabled()) {
				try {
					if (instance != null || field.isStatic()) {
						field.setAccessible(true);
						field.set(instance, get(asset.value()));
					}
				} catch (ReflectionException e) {
					throw new IllegalArgumentException("Failed to set field '"
							+ field.getName() + "' of class '" + clazz.getName()
							+ "' to the loaded asset value.", e);
				}
			}
		}

		if (clazz.getSuperclass() != null) {
			injectAssets(clazz.getSuperclass(), instance);
		}
	}

	private AssetLoaderParameters<?> getAssetLoaderParameters(InjectAsset asset,
			Field field) {
		if (asset.params() == null || asset.params().length() == 0)
			return null;

		AssetLoaderParametersFactory<?> factory = paramFactories
				.get(field.getType());
		if (factory == null)
			throw new IllegalStateException("Arguments for a field of type '"
					+ field.getType()
					+ "' cannot be processed without a corresponding params factory.");

		try {
			return factory.newInstance(asset.value(), asset.params());
		} catch (Exception e) {
			throw new RuntimeException(
					"Error while parsing the params for field '"
							+ field.getName() + "' with the following factory: "
							+ factory.getClass().getName(),
					e);
		}
	}

	public <T> void registerAssetLoaderParametersFactory(Class<T> clazz,
			AssetLoaderParametersFactory<T> factory) {
		paramFactories.put(clazz, factory);
	}

	/** @see #injectAssets(Class, Object) */
	@SuppressWarnings("unchecked")
	public <T> void injectAssets(T container) {
		injectAssets((Class<T>) container.getClass(), container);
	}

	/** @see #getAssetsSet(Class, Object) */
	public void injectAssets(Class<?> container) {
		injectAssets(container, null);
	}

	/**
	 * These factories are responsible for parsing the
	 * {@link InjectAsset#params()} to {@link AssetLoaderParameters}.
	 *
	 * @param <T>
	 */
	public static interface AssetLoaderParametersFactory<T> {
		public AssetLoaderParameters<T> newInstance(String path, String params);
	}

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public static @interface InjectAsset {
		boolean disabled() default false;

		/**
		 * @return the path to the asset to inject.
		 */
		String value();

		String params() default "";
	}

}
