package dev.gg.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;

import dev.gg.core.ProjektGG;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * A basic screen that automatically takes care of asset loading when used in
 * conjunction with {@link ProjektGG}.
 * <p>
 * Assets have to be marked via the {@link Asset}-Annotation and then the game
 * loads these the first time the screen is {@link Screen#show() shown}. The
 * {@link #onInit()}-method is called after the loading process to allow
 * initializing the screen. To manually load the assets i.e. when implementing a
 * loading screen, one has to call {@link #finishLoading()} after finishing to
 * load the assets.
 */
public abstract class BaseScreen implements Screen {

	protected Color backgroundColor = Color.BLACK;
	protected ProjektGG game;
	protected AnnotationAssetManager assetManager;
	/**
	 * Indicates whether the assets already got loaded.
	 */
	private boolean loaded = false;

	/**
	 * Initializes the screen. Is automatically called by {@link ProjectGG}.
	 * 
	 * @param game
	 * @param assetManager
	 */
	public final void init(ProjektGG game,
			AnnotationAssetManager assetManager) {
		this.game = game;
		this.assetManager = assetManager;
	}

	/**
	 * Is called after the assets got loaded. Normally the game takes care of
	 * this but when an external loading screen is used this method has to be
	 * called afterwards.
	 */
	public void finishLoading() {
		loaded = true;
		onInit();
	}

	/**
	 * Is called after all assets annotated with {@link Asset} are loaded.
	 */
	protected abstract void onInit();

	/**
	 * @return Whether the screens assets already got loaded.
	 */
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void pause() {
		// unused
	}

	@Override
	public void resume() {
		// unused
	}

	@Override
	public void resize(int width, int height) {
		// isn't needed as the game can't be resized
	}

}
