package de.gg.engine.asset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;

import de.gg.LibgdxUnitTest;
import de.gg.engine.asset.AnnotationAssetManager.AssetLoaderParametersFactory;
import de.gg.engine.asset.AnnotationAssetManager.InjectAsset;
import de.gg.engine.asset.JSONLoader.JSONLoaderParameter;
import de.gg.engine.utils.json.JsonTestObject;

public class CustomAssetTypesTest extends LibgdxUnitTest {

	public AnnotationAssetManager createAssetManager() {
		FileHandleResolver resolver = new InternalFileHandleResolver();
		AnnotationAssetManager aM = new AnnotationAssetManager(resolver);
		aM.setLoader(Text.class,
				new TextLoader(new InternalFileHandleResolver()));
		aM.setLoader(JSON.class,
				new JSONLoader(new InternalFileHandleResolver()));
		aM.registerAssetLoaderParametersFactory(JSON.class,
				new AssetLoaderParametersFactory<JSON>() {
					@Override
					public AssetLoaderParameters<JSON> newInstance(String path,
							String params) {
						return new JSONLoaderParameter(JsonTestObject.class);
					}
				});

		return aM;
	}

	@SuppressWarnings("deprecation")
	@Test
	public void test() {
		AnnotationAssetManager aM = createAssetManager();

		// Loading assets for object & injecting them
		AssetHolder holder = new AssetHolder();
		aM.load(AssetHolder.class);
		aM.finishLoading();

		aM.injectAssets(holder);

		assertNotNull(holder.text);
		assertNotNull(holder.json);

		// Check if assets got actually loaded
		assertTrue(aM.isLoaded("text.txt"));
		assertTrue(aM.isLoaded("json.json"));

		// Check values
		JsonTestObject o = new JsonTestObject();
		o.date = new Date(2018, 11, 30);
		o.i = 35;
		o.string = "xyz";
		o.string2 = "abc";

		assertEquals("Hello World!\nHow are you?", holder.text.getString());
		assertEquals(o, holder.json.getData(JsonTestObject.class));
	}

	public static class AssetHolder {
		@InjectAsset("text.txt")
		private Text text;

		@InjectAsset(value = "json.json", params = "a")
		private JSON json;
	}

}
