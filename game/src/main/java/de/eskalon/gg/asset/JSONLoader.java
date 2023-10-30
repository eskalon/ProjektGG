package de.eskalon.gg.asset;

import java.lang.reflect.Type;
import java.nio.charset.Charset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import de.eskalon.gg.asset.JSONLoader.JSONLoaderParameter;

/**
 * A simple asset loader for {@linkplain JSON JSON} files.
 * <p>
 * Uses the UTF-8-encoding for the loaded files if supported by the local JVM.
 *
 * @author damios
 */
public class JSONLoader
		extends AsynchronousAssetLoader<JSON, JSONLoaderParameter> {

	private static final Charset CHARSET = Charset.isSupported("UTF-8")
			? Charset.forName("UTF-8")
			: Charset.defaultCharset();
	private SimpleJSONParser jsonParser;

	public JSONLoader(FileHandleResolver resolver) {
		super(resolver);

		this.jsonParser = new SimpleJSONParser();
	}

	protected JSON currentlyLoadedObject;

	@Override
	public void loadAsync(AssetManager manager, String fileName,
			FileHandle file, JSONLoaderParameter param) {
		this.currentlyLoadedObject = new JSON(
				jsonParser.parseFromJson(new String(file.readBytes(), CHARSET),
						param.clazz == null ? param.type : param.clazz));
	}

	@Override
	public JSON loadSync(AssetManager manager, String fileName, FileHandle file,
			JSONLoaderParameter param) {
		JSON object = this.currentlyLoadedObject;
		this.currentlyLoadedObject = null;

		return object;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName,
			FileHandle file, JSONLoaderParameter param) {
		return null;
	}

	public static class JSONLoaderParameter
			extends AssetLoaderParameters<JSON> {

		public JSONLoaderParameter(Class<?> clazz) {
			this.type = null;
			this.clazz = clazz;
		}

		public JSONLoaderParameter(Type type) {
			this.type = type;
			this.clazz = null;
		}

		public Class<?> clazz;
		public Type type;
	}

}
