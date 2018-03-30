package de.gg.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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

import de.gg.camera.CameraWrapper;
import de.gg.exception.ScreenNotFoundException;
import de.gg.input.GameInputMultiplexer;
import de.gg.network.NetworkHandler;
import de.gg.screen.BaseScreen;
import de.gg.screen.BaseUIScreen;
import de.gg.screen.GameInHouseScreen;
import de.gg.screen.GameLoadingScreen;
import de.gg.screen.GameMapScreen;
import de.gg.screen.GameRoundendScreen;
import de.gg.screen.LoadingScreen;
import de.gg.screen.LobbyCreationScreen;
import de.gg.screen.LobbyScreen;
import de.gg.screen.MainMenuScreen;
import de.gg.screen.ServerBrowserScreen;
import de.gg.screen.SplashScreen;
import de.gg.setting.GameSettings;
import de.gg.util.EventQueueBus;
import de.gg.util.asset.Text;
import de.gg.util.asset.TextLoader;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

/**
 * This class starts the game by creating all the necessary screens and then
 * displaying the menu. The assets of the screens are loaded automatically when
 * the screens are first shown, but can also be loaded by the
 * {@link LoadingScreen LoadingScreen}.
 * <p>
 * Only {@link BaseScreen}s are supported.
 */
public class ProjektGG extends Game {

	public static final String name = "ProjektGG";
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
	private CameraWrapper gameCamera;

	private GameSettings settings;

	private boolean debug, showSplashscreen;

	private GameInputMultiplexer inputProcessor = new GameInputMultiplexer();

	private Skin uiSkin;

	private GameSession currentSession;

	/**
	 * Event bus. All events are queued first and then taken care of in the
	 * rendering thread.
	 */
	private EventQueueBus eventBus;
	private NetworkHandler networkHandler;

	public ProjektGG(boolean debug, boolean showSplashscreen) {
		super();

		this.debug = debug;
		this.showSplashscreen = showSplashscreen;
	}

	@Override
	public final void create() {
		if (debug)
			Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
		else
			Gdx.app.setLogLevel(Gdx.app.LOG_INFO);

		// Initialize sprite batch
		this.batch = new SpriteBatch();

		// Initialize asset manager
		FileHandleResolver resolver = new InternalFileHandleResolver();
		this.assetManager.setLoader(FreeTypeFontGenerator.class,
				new FreeTypeFontGeneratorLoader(resolver));
		this.assetManager.setLoader(BitmapFont.class, ".ttf",
				new FreetypeFontLoader(resolver));
		this.assetManager.setLoader(Text.class,
				new TextLoader(new InternalFileHandleResolver()));

		this.viewportWidth = Gdx.graphics.getWidth();
		this.viewportHeight = Gdx.graphics.getHeight();

		// Initialize cameras
		this.uiCamera = new OrthographicCamera(viewportWidth, viewportHeight);
		this.uiCamera.translate(viewportWidth / 2, viewportHeight / 2, 0);
		this.uiCamera.update();

		this.gameCamera = new CameraWrapper(
				new PerspectiveCamera(67, viewportWidth, viewportHeight));
		this.gameCamera.getCamera().translate(viewportWidth / 2,
				viewportHeight / 2, 0);
		// this.camera.update();
		this.batch.setProjectionMatrix(this.gameCamera.getCamera().combined);

		// Load game settings
		this.settings = new GameSettings("projekt-gg");

		// Create the event bus
		this.eventBus = new EventQueueBus();

		// Create the network handler
		this.networkHandler = new NetworkHandler(eventBus);

		// Set input processor
		Gdx.input.setInputProcessor(inputProcessor);

		// Add screens
		addScreen("splash", new SplashScreen());
		addScreen("mainMenu", new MainMenuScreen());
		addScreen("loading", new LoadingScreen());
		addScreen("gameLoading", new GameLoadingScreen());
		addScreen("serverBrowser", new ServerBrowserScreen());
		addScreen("lobby", new LobbyScreen());
		addScreen("lobbyCreation", new LobbyCreationScreen());
		addScreen("map", new GameMapScreen());
		addScreen("house", new GameInHouseScreen());
		addScreen("roundEnd", new GameRoundendScreen());

		// Push screen
		if (showSplashscreen)
			pushScreen("splash");
		else
			pushScreen("loading");
	}

	@Override
	public void render() {
		// Takes care of posting the events in the rendering thread
		eventBus.distributeEvents();

		super.render();
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
	 * {@link Screen#hide()} is called on the previously {@linkplain Game#screen
	 * active screen} and {@link Screen#show()} is called on the new active
	 * screen.
	 * 
	 * @param name
	 *            The name of the pushed screen.
	 */
	public void pushScreen(String name) {
		BaseScreen pushedScreen = this.screens.get(name);

		if (pushedScreen == null) {
			throw new ScreenNotFoundException("Could not find a screen named '"
					+ name
					+ "'. Add the screen via #addScreen(String, BaseScreen) first.");
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
					+ "'. Add the screen via #addScreen(String, BaseScreen) first.");
		}

		return screen;
	}

	@Override
	public final void dispose() {
		this.screen = null;
		for (Screen s : screens.values()) {
			s.pause();
			s.dispose();
		}

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
	public CameraWrapper getGameCamera() {
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
	 * @return The events bus. See {@link EventQueueBus}. Events are processed
	 *         in the rendering thread.
	 */
	public EventQueueBus getEventBus() {
		return eventBus;
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
	 * @return The current game session. Null if no session is played at the
	 *         moment.
	 * @see #getCurrentMultiplayerSession()
	 */
	public GameSession getCurrentSession() {
		return currentSession;
	}

	/**
	 * @return The current multiplayer game session.
	 * @see #getCurrentSession()
	 */
	public MultiplayerSession getCurrentMultiplayerSession() {
		return (MultiplayerSession) currentSession;
	}

	public void setCurrentSession(MultiplayerSession session) {
		this.currentSession = session;
	}

	/**
	 * @return The network handler for this client. Can be null.
	 */
	public NetworkHandler getNetworkHandler() {
		return networkHandler;
	}

	public void setNetworkHandler(NetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	/**
	 * Returns the input multiplexer of the game. Should be used instead of
	 * {@link Input#setInputProcessor(InputProcessor)}.
	 * 
	 * @return The game's input multiplexer.
	 */
	public GameInputMultiplexer getInputMultiplexer() {
		return inputProcessor;
	}

	/**
	 * @return Whether the debug flag is set and thus debug stuff should get
	 *         rendered.
	 */
	public boolean showDebugStuff() {
		return debug;
	}

}