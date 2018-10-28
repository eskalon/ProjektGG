package de.gg.engine.ui.screens;

import com.badlogic.gdx.Screen;

import de.gg.engine.asset.AnnotationAssetManager;
import de.gg.engine.asset.AnnotationAssetManager.InjectAsset;
import de.gg.engine.core.ScreenGame;

/**
 * A very basic screen that automatically takes care of loading its assets when
 * used in conjunction with {@link ScreenGame}.
 * <p>
 * Assets have to be marked via the {@link InjectAsset}-Annotation and then the
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
	 * Has to get called after the assets got loaded to mark this screen as
	 * loaded. Normally the game takes care of this, but when an external
	 * loading screen is used this method has to get called afterwards.
	 */
	public void finishLoading() {
		loaded = true;
		onInit();
	}

	/**
	 * Is called <i>once</i> after all assets annotated with {@link InjectAsset} are
	 * loaded and injected.
	 */
	protected abstract void onInit();

	/**
	 * @return Whether the screens assets already got loaded.
	 */
	public boolean isLoaded() {
		return loaded;
	}

}
