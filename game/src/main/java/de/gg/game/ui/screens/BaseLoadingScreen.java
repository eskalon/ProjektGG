package de.gg.game.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;

import de.gg.engine.asset.AnnotationAssetManager;
import de.gg.engine.asset.AnnotationAssetManager.InjectAsset;
import de.gg.engine.ui.screens.BaseScreen;
import de.gg.game.core.ProjektGG;

/**
 * This screen takes care of loading certain assets. Assets have to be
 * {@linkplain AssetManager#load(AssetDescriptor) added to the loading queue} in
 * the {@link #initAssets} method.
 */
public abstract class BaseLoadingScreen extends BaseScreen<ProjektGG> {

	// Assets of the loading screen itself
	protected Texture backgroundTexture;
	@InjectAsset("ui/images/bar-top.png")
	private Texture topBarTexture;
	@InjectAsset("ui/images/bar-bottom.png")
	private Texture bottomBarTexture;
	private float progress;

	@Override
	protected void onInit() {
		initAssets(game.getAssetManager());
	}

	/**
	 * Add the assets, that the loading screen should load, to the
	 * {@linkplain AssetManager#load(AssetDescriptor) loading queue} in this
	 * method.
	 *
	 * @param assetManager
	 *            the asset manger to use.
	 */
	protected abstract void initAssets(AnnotationAssetManager assetManager);

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
			onFinishedLoading(game.getAssetManager());
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
	 * This method should
	 * {@linkplain BaseScreen#finishLoading(AnnotationAssetManager) notify} the
	 * loaded assets and push the next screen.
	 *
	 * @param assetManager
	 *            the asset manager to retrieve the loaded assets.
	 */
	protected abstract void onFinishedLoading(
			AnnotationAssetManager assetManager);

	@Override
	public void dispose() {
		// unused
	}

}
