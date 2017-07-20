package dev.gg.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.util.CursorManager;

import dev.gg.exception.ScreenNotFoundException;
import dev.gg.network.MultiplayerSession;
import dev.gg.screen.BaseScreen;
import dev.gg.screen.BaseUIScreen;
import dev.gg.screen.GameHouseScreen;
import dev.gg.screen.GameMapScreen;
import dev.gg.screen.GameRoundendScreen;
import dev.gg.screen.LoadingScreen;
import dev.gg.screen.LobbyCreationScreen;
import dev.gg.screen.LobbyScreen;
import dev.gg.screen.MainMenuScreen;
import dev.gg.screen.ServerBrowserScreen;
import dev.gg.screen.SplashScreen;
import dev.gg.setting.GameSettings;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

/**
 * This class starts the game by creating all the necessary screens and then
 * displaying the menu.
 * <p>
 * Only {@link BaseScreen}s are supported.
 *
 */
public class ProjektGG extends Game {

	private SpriteBatch batch;
	/**
	 * The asset manager.
	 */
	private final AnnotationAssetManager assetManager = new AnnotationAssetManager(
			new InternalFileHandleResolver());
	/**
	 * A map with all initialized screens.
	 */
	private Map<String, BaseScreen> screens = new ConcurrentHashMap<>();

	private static int viewportWidth;
	private static int viewportHeight;

	private OrthographicCamera uiCamera;
	private PerspectiveCamera gameCamera;

	private GameSettings settings;

	private CursorManager cursorManager;

	private boolean debug, showSplashscreen;

	private Skin uiSkin;

	private GameSession session;

	public ProjektGG(boolean debug, boolean showSplashscreen) {
		super();

		this.debug = debug;
		this.showSplashscreen = showSplashscreen;
	}

	@Override
	public final void create() {
		// Initialize sprite batch
		this.batch = new SpriteBatch();

		// Initialize asset manager
		FileHandleResolver resolver = new InternalFileHandleResolver();
		this.assetManager.setLoader(FreeTypeFontGenerator.class,
				new FreeTypeFontGeneratorLoader(resolver));
		this.assetManager.setLoader(BitmapFont.class, ".ttf",
				new FreetypeFontLoader(resolver));

		this.viewportWidth = Gdx.graphics.getWidth();
		this.viewportHeight = Gdx.graphics.getHeight();

		// Initialize cameras
		this.uiCamera = new OrthographicCamera(viewportWidth, viewportHeight);
		this.uiCamera.translate(viewportWidth / 2, viewportHeight / 2, 0);
		this.uiCamera.update();

		this.gameCamera = new PerspectiveCamera(0, viewportWidth,
				viewportHeight);
		this.gameCamera.translate(viewportWidth / 2, viewportHeight / 2, 0);
		// this.camera.update();
		this.batch.setProjectionMatrix(this.gameCamera.combined);

		// Create new cursor manager
		this.cursorManager = new CursorManager();

		// Load game settings
		this.settings = new GameSettings("projekt-gg");

		// Add screens
		addScreen("splash", new SplashScreen());
		addScreen("mainMenu", new MainMenuScreen());
		addScreen("loading", new LoadingScreen());
		addScreen("serverBrowser", new ServerBrowserScreen());
		addScreen("lobby", new LobbyScreen());
		addScreen("lobbyCreation", new LobbyCreationScreen());
		addScreen("map", new GameMapScreen());
		addScreen("house", new GameHouseScreen());
		addScreen("roundEnd", new GameRoundendScreen());

		// Push screen
		if (showSplashscreen)
			pushScreen("splash");
		else
			pushScreen("loading");
	}

	/**
	 * Adds a screen to the game.
	 * 
	 * 
	 * @param name
	 *            The name of the screen.
	 * @param screen
	 *            The screen.
	 */
	public void addScreen(String name, BaseScreen screen) {
		screen.init(this, this.getAssetManager());

		this.screens.put(name, screen);
	}

	/**
	 * Pushes a screen to be the active screen. The screen has to be added to
	 * the game beforehand via {@link #addScreen(String, BaseScreen)}.
	 * <p>
	 * {@link Screen#hide()} is called on any old activeScreen, and
	 * {@link Screen#show()} is called on the new activeScreen, if any.
	 * 
	 * @param name
	 *            The name of the pushed screen.
	 */
	public void pushScreen(String name) {
		BaseScreen pushedScreen = this.screens.get(name);

		if (pushedScreen == null) {
			throw new ScreenNotFoundException("Could not find a screen named '"
					+ name
					+ "'. Add the screen first via #addScreen(String, BaseScreen).");
		}

		if (screen != null) {
			screen.hide();
		}

		if (!pushedScreen.isLoaded()) {
			assetManager.load(pushedScreen);
			assetManager.finishLoading();
			pushedScreen.finishLoading();
		}

		pushedScreen.show();
		this.screen = pushedScreen;
	}

	/**
	 * Returns a screen in the game.
	 * 
	 * @param name
	 *            The name of the screen.
	 * @return The screen.
	 */
	public BaseScreen getScreen(String name) {
		BaseScreen screen = this.screens.get(name);

		if (screen == null) {
			throw new ScreenNotFoundException("Could not find a screen named '"
					+ name
					+ "'. Add the screen first via #addScreen(String, BaseScreen).");
		}

		return screen;
	}

	@Override
	public final void dispose() {
		super.dispose();
		this.batch.dispose();
	}

	/**
	 * @return The asset manager used by the game.
	 */
	public AnnotationAssetManager getAssetManager() {
		return this.assetManager;
	}

	/**
	 * @return The camera used in the actual game.
	 */
	public PerspectiveCamera /* CameraWrapper */ getGameCamera() {
		return this.gameCamera;
	}

	/**
	 * @return The camera used by the UI screens.
	 * @see BaseUIScreen#render(float)
	 */
	public Camera getUICamera() {
		return this.uiCamera;
	}

	/**
	 * @return An instance of the game settings handler.
	 */
	public GameSettings getSettings() {
		return settings;
	}

	/**
	 * Sets the UI skin.
	 * 
	 * @param skin
	 *            The UI skin.
	 */
	public void setUISkin(Skin skin) {
		this.uiSkin = skin;
	}

	/**
	 * @return The UI skin that got loaded in the {@link LoadingScreen}.
	 */
	public Skin getUISkin() {
		return uiSkin;
	}

	public CursorManager getCursorManager() {
		return this.cursorManager;
	}

	public SpriteBatch getSpriteBatch() {
		return batch;
	}

	/**
	 * @return The initial viewport width.
	 */
	public int getViewportWidth() {
		return this.viewportWidth;
	}

	/**
	 * @return The initial viewport height.
	 */
	public int getViewportHeight() {
		return this.viewportHeight;
	}

	/**
	 * @return The current game session.
	 */
	public GameSession getCurrentSession() {
		return session;
	}

	/**
	 * @return The current multiplayer game session.
	 */
	public MultiplayerSession getCurrentMultiplayerSession() {
		return (MultiplayerSession) session;
	}

	public void setCurrentSession(MultiplayerSession session) {
		this.session = session;
	}

	/**
	 * @return Whether the debug flag is set and thus debug stuff should get
	 *         rendered.
	 */
	public boolean showDebugStuff() {
		return debug;
	}

}
