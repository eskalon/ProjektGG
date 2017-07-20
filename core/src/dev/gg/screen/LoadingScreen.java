package dev.gg.screen;

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

import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen takes care of loading the assets for the game.
 */
public class LoadingScreen extends BaseScreen {

	// Loading screen assets
	@Asset(Texture.class)
	private static final String BACKGROUND_TEXTURE_PATH = "ui/backgrounds/market.jpg";
	@Asset(Texture.class)
	private static final String BAR_ORANGE_TEXTURE_PATH = "ui/images/bar-top.png";
	@Asset(Texture.class)
	private static final String BAR_BLUE_TEXTURE_PATH = "ui/images/bar-bottom.png";
	private Texture backgroundTexture;
	private Texture logoTexture;
	private Texture orangeBarTexture;
	private Texture blueBarTexture;
	private float progress;

	// UI skin assets
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
	protected void onInit() {
		backgroundTexture = assetManager.get(BACKGROUND_TEXTURE_PATH);
		orangeBarTexture = assetManager.get(BAR_ORANGE_TEXTURE_PATH);
		blueBarTexture = assetManager.get(BAR_BLUE_TEXTURE_PATH);

		// Add assets to loading queue
		assetManager.load(MAIN_FONT_19_PATH());
		assetManager.load(MAIN_FONT_22_PATH());
		assetManager.load(LETTER_FONT_20_PATH());
		assetManager.load(HANDWRITTEN_FONT_20_PATH());
		assetManager.load(game.getScreen("serverBrowser"));
		assetManager.load(game.getScreen("lobby"));
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.getSpriteBatch().begin();
		game.getSpriteBatch().setProjectionMatrix(game.getUICamera().combined);

		// Draw the background
		game.getSpriteBatch().draw(this.backgroundTexture, 0, 0,
				game.getViewportWidth(), game.getViewportHeight());

		// Get useful values
		float viewPortWidth = game.getViewportWidth();
		float viewPortHeight = game.getViewportHeight();
		float imageWidth = this.orangeBarTexture.getWidth();
		float imageHeight = this.orangeBarTexture.getHeight();

		// Check if the asset manager is done
		if (game.getAssetManager().update()) {
			onFinishedLoading();
		}

		progress = Interpolation.linear.apply(progress,
				game.getAssetManager().getProgress(), 0.1f);

		// The actual drawing
		game.getSpriteBatch().draw(this.blueBarTexture,
				(viewPortWidth / 2) - (imageWidth / 2) + 1,
				(viewPortHeight / 4) - imageHeight / 2);
		game.getSpriteBatch().draw(this.orangeBarTexture,
				(viewPortWidth / 2) - (imageWidth / 2),
				(viewPortHeight / 4) - imageHeight / 2, imageWidth * progress,
				imageHeight);

		this.game.getSpriteBatch().end();
	}

	private void onFinishedLoading() {
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
		game.getScreen("serverBrowser").finishLoading();

		game.pushScreen("mainMenu");
	}
	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}

}
