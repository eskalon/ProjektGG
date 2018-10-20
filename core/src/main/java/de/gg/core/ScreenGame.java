package de.gg.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.google.common.base.Preconditions;

import de.gg.screens.LoadableScreen;
import de.gg.screens.exception.ScreenNotFoundException;
import de.gg.utils.log.Log;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

/**
 * This class represents a screen based game. The assets of the screens are
 * loaded automatically when the screens are first {@linkplain Screen#show()
 * shown}, but can also be loaded by a custom loading screen.
 * <p>
 * To add a screen {@link #addScreen(String, LoadableScreen)} has to get used;
 * to actually show a screen, push it via {@link #pushScreen(String)}.
 * <p>
 * Only child-classes of {@link LoadableScreen} are supported.
 */
public abstract class ScreenGame<S extends LoadableScreen> extends Game {

	/**
	 * The used asset manager.
	 */
	protected final AnnotationAssetManager assetManager;
	/**
	 * A map with all initialized screens.
	 */
	private Map<String, S> screens = new ConcurrentHashMap<>();

	protected int viewportWidth;
	protected int viewportHeight;

	public ScreenGame() {
		this.assetManager = new AnnotationAssetManager(
				new InternalFileHandleResolver());
	}

	@Override
	public void create() {
		this.viewportWidth = Gdx.graphics.getWidth();
		this.viewportHeight = Gdx.graphics.getHeight();
	}

	/**
	 * Adds a screen to the game. The screen can get initialized via
	 * {@link #onScreenInitialization(LoadableScreen)}.
	 *
	 * @param name
	 *            the name of the screen.
	 * @param screen
	 *            the screen.
	 */
	public final void addScreen(String name, S screen) {
		Preconditions.checkNotNull(screen, "screen cannot be null");
		Preconditions.checkArgument(!name.isEmpty(), "name cannot be empty");

		onScreenInitialization(screen);

		this.screens.put(name, screen);
	}

	protected abstract void onScreenInitialization(S screen);

	/**
	 * Pushes a screen to be the active screen. The screen has to be added to
	 * the game beforehand via {@link #addScreen(String, LoadableScreen)}.
	 * <p>
	 * {@link Screen#hide()} is called on the previously {@linkplain Game#screen
	 * active screen} and {@link Screen#show()} is called on the new active
	 * screen.
	 *
	 * @param name
	 *            the name of the pushed screen.
	 */
	public synchronized final void pushScreen(String name) {
		Preconditions.checkArgument(!name.isEmpty(), "name cannot be empty");

		S pushedScreen = screens.get(name);

		if (pushedScreen == null) {
			throw new ScreenNotFoundException(String.format(
					"No screen with the name '%s' could be found. Add the screen via #addScreen(String, BaseScreen) first.",
					name));
		}

		Gdx.app.postRunnable(() -> {
			Log.debug("Screen", "Screen gepushed: '%s'", name);

			if (screen != null) {
				screen.hide();
			}

			if (!pushedScreen.isLoaded()) {
				assetManager.load(pushedScreen);
				assetManager.finishLoading();
				pushedScreen.finishLoading(assetManager);
			}

			pushedScreen.show();
			screen = pushedScreen;
		});
	}

	/**
	 * Returns a screen in the game.
	 *
	 * @param name
	 *            the name of the screen.
	 * @return the screen.
	 * @throws ScreenNotFoundException
	 *             when the screen isn't found
	 */
	public final S getScreen(String name) {
		Preconditions.checkArgument(!name.isEmpty(), "name cannot be empty");

		S screen = this.screens.get(name);

		if (screen == null) {
			throw new ScreenNotFoundException(String.format(
					"No screen with the name '%s' could be found. Add the screen via #addScreen(String, BaseScreen) first.",
					name));
		}

		return screen;
	}

	@Override
	public void dispose() {
		this.screen = null;
		for (Screen s : screens.values()) {
			s.pause();
			s.dispose();
		}
	}

	/**
	 * @return the asset manager used by the game.
	 */
	public AnnotationAssetManager getAssetManager() {
		return this.assetManager;
	}

	/**
	 * @return the initial viewport width.
	 */
	public int getViewportWidth() {
		return this.viewportWidth;
	}

	/**
	 * @return the initial viewport height.
	 */
	public int getViewportHeight() {
		return this.viewportHeight;
	}

}
