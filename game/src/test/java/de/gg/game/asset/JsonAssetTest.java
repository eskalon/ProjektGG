package de.gg.game.asset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;

import de.eskalon.commons.LibgdxUnitTest;
import de.eskalon.commons.asset.AnnotationAssetManager;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.asset.AnnotationAssetManager.AssetLoaderParametersFactory;
import de.gg.game.asset.JSONLoader.JSONLoaderParameter;

public class JsonAssetTest extends LibgdxUnitTest {

	public AnnotationAssetManager createAssetManager() {
		FileHandleResolver resolver = new InternalFileHandleResolver();
		AnnotationAssetManager aM = new AnnotationAssetManager(resolver);
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
		aM.loadAnnotatedAssets(AssetHolder.class);
		aM.finishLoading();

		aM.injectAssets(holder);

		assertNotNull(holder.json);

		// Check if assets got actually loaded
		assertTrue(!aM.isLoaded("text.txt"));
		assertTrue(aM.isLoaded("json.json"));

		// Check values
		JsonTestObject o = new JsonTestObject();
		o.date = new Date(2018, 11, 30);
		o.i = 35;
		o.string = "xyz";
		o.string2 = "abc";

		assertEquals(o, holder.json.getData(JsonTestObject.class));
	}

	public static class AssetHolder {
		@Asset(value = "json.json", params = "a")
		private JSON json;
	}

}
