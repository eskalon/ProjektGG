package de.gg.screens;

import com.badlogic.gdx.Screen;

import de.gg.core.ScreenGame;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * A very basic screen that automatically takes care of loading its assets when
 * used in conjunction with {@link ScreenGame}.
 * <p>
 * Assets have to be marked via the {@link Asset}-Annotation and then the
 * {@linkplain ScreenGame game} loads these the first time the screen is
 * {@link Screen#show() shown}. The
 * {@link #onInit(AnnotationAssetManager)}-method is called after the loading
 * process to allow initializing the screen.
 * <p>
 * To manually load the assets i.e. when implementing a loading screen, one has
 * to call {@link #finishLoading(AnnotationAssetManager)} after finishing to
 * load the assets.
 */
public abstract class LoadableScreen implements Screen {

	/**
	 * Indicates whether the assets already got loaded.
	 */
	private boolean loaded = false;

	/**
	 * Has to get called after the assets got loaded. Normally the game takes
	 * care of this, but when an external loading screen is used this method has
	 * to get called afterwards.
	 * 
	 * @param assetManager
	 *            the used asset manager.
	 */
	public void finishLoading(AnnotationAssetManager assetManager) {
		loaded = true;
		onInit(assetManager);
	}

	/**
	 * Is called after all assets annotated with {@link Asset} are loaded.
	 * 
	 * @param assetManager
	 *            this asset manager can be used to retrieve the loaded assets.
	 */
	protected abstract void onInit(AnnotationAssetManager assetManager);

	/**
	 * @return Whether the screens assets already got loaded.
	 */
	public boolean isLoaded() {
		return loaded;
	}

}
