package de.gg.engine.asset;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

import de.gg.LibgdxUnitTest;
import de.gg.engine.asset.AnnotationAssetManager.InjectAsset;

public class AnnotationAssetManagerTest extends LibgdxUnitTest {

	public AnnotationAssetManager createAssetManager() {
		FileHandleResolver resolver = new InternalFileHandleResolver();
		AnnotationAssetManager aM = new AnnotationAssetManager(resolver);
		aM.setLoader(FreeTypeFontGenerator.class,
				new FreeTypeFontGeneratorLoader(resolver));
		aM.setLoader(BitmapFont.class, ".ttf",
				new FreetypeFontLoader(resolver));

		aM.registerAssetLoaderParametersFactory(BitmapFont.class,
				new BitmapFontAssetLoaderParametersFactory());

		return aM;
	}

	@Test
	public void test() {
		AnnotationAssetManager aM = createAssetManager();

		// Loading assets for object & injecting them
		AssetHolder holder = new AssetHolder();
		aM.load(AssetHolder.class);
		aM.finishLoading();

		aM.injectAssets(holder);

		assertNotNull(holder.test1a);
		assertNotNull(holder.test1b);
		assertNotNull(AssetHolder.test2);

		holder.test1a = null;
		holder.test1b = null;
		AssetHolder.test2 = null;

		// Only inject the static assets
		aM.injectAssets(AssetHolder.class);
		assertNull(holder.test1a);
		assertNull(holder.test1b);
		assertNotNull(AssetHolder.test2);

		// Check if assets got actually loaded
		assertTrue(aM.isLoaded("test.png"));
		assertTrue(aM.isLoaded("font/OpenSans.ttf"));
		assertTrue(aM.isLoaded("test2.png"));
	}

	public static class AssetHolder {
		@InjectAsset("test.png")
		private Texture test1a;

		@InjectAsset(value = "font/OpenSans.ttf", params = "19")
		private BitmapFont test1b;

		@InjectAsset("test2.png")
		private static Texture test2;
	}

}
