package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;

import de.gg.render.TestShader;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen takes care of loading the assets for all screens except the
 * ingame ones.
 */
public class LoadingScreen extends BaseLoadingScreen {

	// Assets for the UI skin
	public final AssetDescriptor<BitmapFont> MAIN_FONT_19_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/AlemdraSC/AlmendraSC-Regular.ttf";
		font.fontParameters.size = 19;
		return new AssetDescriptor<BitmapFont>("mainFont19.ttf",
				BitmapFont.class, font);
	}
	public final AssetDescriptor<BitmapFont> MAIN_FONT_22_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/AlemdraSC/AlmendraSC-Regular.ttf";
		font.fontParameters.size = 22;
		return new AssetDescriptor<BitmapFont>("mainFont22.ttf",
				BitmapFont.class, font);
	}
	public final AssetDescriptor<BitmapFont> LETTER_FONT_20_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/Fredericka_the_Great/FrederickatheGreat-Regular.ttf";
		font.fontParameters.size = 20;
		return new AssetDescriptor<BitmapFont>("letterFont20.ttf",
				BitmapFont.class, font);
	}
	public final AssetDescriptor<BitmapFont> HANDWRITTEN_FONT_20_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/ReenieBeanie/ReenieBeanie.ttf";
		font.fontParameters.size = 20;
		return new AssetDescriptor<BitmapFont>("handwrittenFont20.ttf",
				BitmapFont.class, font);
	}
	private final String SKIN_PATH = "ui/skin/skin.json";
	private final String SKIN_TEXTURE_ATLAS_PATH = "ui/skin/skin.atlas";

	@Override
	protected void initAssets() {
		// Add assets to loading queue
		assetManager.load(MAIN_FONT_19_PATH());
		assetManager.load(MAIN_FONT_22_PATH());
		assetManager.load(LETTER_FONT_20_PATH());
		assetManager.load(HANDWRITTEN_FONT_20_PATH());
		assetManager.load(game.getScreen("mainMenu"));
		assetManager.load(game.getScreen("serverBrowser"));
		assetManager.load(game.getScreen("lobby"));
		assetManager.load(game.getScreen("lobbyCreation"));
	}
	@Override
	protected void onFinishedLoading() {
		BitmapFont main19Font = assetManager.get(MAIN_FONT_19_PATH());
		BitmapFont main22Font = assetManager.get(MAIN_FONT_22_PATH());
		BitmapFont letter20Font = assetManager.get(LETTER_FONT_20_PATH());
		BitmapFont handwritten20Font = assetManager
				.get(HANDWRITTEN_FONT_20_PATH());

		ObjectMap<String, Object> fontMap = new ObjectMap<String, Object>();
		fontMap.put("main-19", main19Font);
		fontMap.put("main-22", main22Font);
		fontMap.put("letter-20", letter20Font);
		fontMap.put("handwritten-20", handwritten20Font);
		assetManager.load(SKIN_PATH, Skin.class,
				new SkinLoader.SkinParameter(SKIN_TEXTURE_ATLAS_PATH, fontMap));
		assetManager.finishLoadingAsset(SKIN_PATH);

		// game.setUISkin(assetManager.get(SKIN_PATH));
		VisUI.load();
		game.setUISkin(VisUI.getSkin());

		// Notify loaded screens
		game.getScreen("mainMenu").finishLoading();
		game.getScreen("serverBrowser").finishLoading();
		game.getScreen("lobby").finishLoading();
		game.getScreen("lobbyCreation").finishLoading();

		game.pushScreen("mainMenu");
	}

}
