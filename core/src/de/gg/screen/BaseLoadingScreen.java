package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;

import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen takes care of loading certain assets. Assets have to be
 * {@linkplain AssetManager#load(AssetDescriptor) added to the loading queue} in
 * the {@link #initAssets} method.
 */
public abstract class BaseLoadingScreen extends BaseScreen {

	// Assets of the loading screen itself
	@Asset(Texture.class)
	private static final String BAR_TOP_TEXTURE_PATH = "ui/images/bar-top.png";
	@Asset(Texture.class)
	private static final String BAR_BOTTOM_TEXTURE_PATH = "ui/images/bar-bottom.png";
	private final String BACKGROUND_TEXTURE_PATH;

	protected Texture backgroundTexture;
	private Texture topBarTexture;
	private Texture bottomBarTexture;
	private float progress;

	protected BaseLoadingScreen(String backgroundTexturePath) {
		this.BACKGROUND_TEXTURE_PATH = backgroundTexturePath;
	}

	@Override
	protected void onInit() {
		// since parent classes aren't automatically loaded, this screen has to
		// take care of loading its own assets itself.
		assetManager.load(BACKGROUND_TEXTURE_PATH, Texture.class);
		assetManager.load(BaseLoadingScreen.class);
		assetManager.finishLoading();

		backgroundTexture = assetManager.get(BACKGROUND_TEXTURE_PATH);
		topBarTexture = assetManager.get(BAR_TOP_TEXTURE_PATH);
		bottomBarTexture = assetManager.get(BAR_BOTTOM_TEXTURE_PATH);

		initAssets();
	}

	/**
	 * Add the assets, that the loading screen should load, to the
	 * {@linkplain AssetManager#load(AssetDescriptor) loading queue} in this
	 * method.
	 */
	protected abstract void initAssets();

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.getSpriteBatch().begin();
		game.getSpriteBatch().setProjectionMatrix(game.getUICamera().combined);

		// Draw the background
		if (backgroundTexture != null)
			game.getSpriteBatch().draw(this.backgroundTexture, 0, 0,
					game.getViewportWidth(), game.getViewportHeight());

		// Get useful values
		float viewPortWidth = game.getViewportWidth();
		float viewPortHeight = game.getViewportHeight();
		float imageWidth = this.topBarTexture.getWidth();
		float imageHeight = this.topBarTexture.getHeight();

		// Check if the asset manager is done
		if (game.getAssetManager().update()) {
			onFinishedLoading();
		}

		progress = Interpolation.linear.apply(progress,
				game.getAssetManager().getProgress(), 0.1f);

		// The actual drawing
		game.getSpriteBatch().draw(this.bottomBarTexture,
				(viewPortWidth / 2) - (imageWidth / 2) + 1,
				(viewPortHeight / 4) - imageHeight / 2);
		game.getSpriteBatch().draw(this.topBarTexture,
				(viewPortWidth / 2) - (imageWidth / 2),
				(viewPortHeight / 4) - imageHeight / 2, imageWidth * progress,
				imageHeight);

		this.game.getSpriteBatch().end();
	}

	/**
	 * This method should {@linkplain BaseScreen#finishLoading() notify} the
	 * loaded assets and push the next screen.
	 */
	protected abstract void onFinishedLoading();

	@Override
	public void show() {
		// unused
	}

	@Override
	public void hide() {
		// unused
	}

	@Override
	public void dispose() {
		// unused
	}

}
