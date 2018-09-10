package de.gg.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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

import de.gg.input.GameInputMultiplexer;
import de.gg.network.GameClient;
import de.gg.network.GameServer;
import de.gg.screens.BaseScreen;
import de.gg.screens.BaseUIScreen;
import de.gg.screens.CreditsScreen;
import de.gg.screens.GameInHouseScreen;
import de.gg.screens.GameLoadingScreen;
import de.gg.screens.GameMapScreen;
import de.gg.screens.GameRoundendScreen;
import de.gg.screens.GameVoteScreen;
import de.gg.screens.LoadingScreen;
import de.gg.screens.LobbyCreationScreen;
import de.gg.screens.LobbyScreen;
import de.gg.screens.MainMenuScreen;
import de.gg.screens.ServerBrowserScreen;
import de.gg.screens.SettingsScreen;
import de.gg.screens.SplashScreen;
import de.gg.setting.GameSettings;
import de.gg.ui.rendering.CameraWrapper;
import de.gg.utils.EventQueueBus;
import de.gg.utils.Log;
import de.gg.utils.asset.JSON;
import de.gg.utils.asset.JSONLoader;
import de.gg.utils.asset.Text;
import de.gg.utils.asset.TextLoader;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

/**
 * This class starts the game by creating all the necessary screens and then
 * displaying the {@link SplashScreen} or the {@link LoadingScreen} depending on
 * the {@link #showSplashscreen}-flag.
 * <p>
 * The assets of the screens are loaded when the {@link LoadingScreen} is shown.
 * <p>
 * Only {@link BaseScreen}s are supported.
 */
public class ProjektGG extends ScreenGame<BaseScreen> {

	public static final String NAME = "ProjektGG";
	/**
	 * the version the application is running on. Set via the jar manifest. Is
	 * <code>Development</code> if the game is started in a development
	 * environment.
	 */
	public final String VERSION;
	/**
	 * Whether the application is running in a development environment. Checks
	 * if a {@linkplain #VERSION version} is set in the jar manifest.
	 */
	public final boolean IN_DEV_ENV;

	private SpriteBatch batch;

	private OrthographicCamera uiCamera;
	private CameraWrapper gameCamera;

	private GameSettings settings;

	private boolean debug, showSplashscreen, fpsCounter;

	private GameInputMultiplexer inputProcessor;

	private Skin uiSkin;

	/**
	 * Event bus. All events are queued first and then taken care of in the
	 * rendering thread.
	 */
	private EventQueueBus eventBus;

	private GameServer server;
	private GameClient client;

	public ProjektGG(boolean debug, boolean showSplashscreen,
			boolean fpsCounter) {
		IN_DEV_ENV = getClass().getPackage().getImplementationVersion() == null;
		VERSION = IN_DEV_ENV ? "Development"
				: getClass().getPackage().getImplementationVersion();

		this.debug = debug;
		this.showSplashscreen = showSplashscreen;
		this.fpsCounter = fpsCounter;
	}

	@Override
	public final void create() {
		super.create();

		if (debug)
			Log.enableDebugLogging();
		else
			Log.disableDebugLogging();

		Log.info("Start ", "Version: '%s', In Dev Environment: '%b'", VERSION,
				IN_DEV_ENV);

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
		this.assetManager.setLoader(JSON.class,
				new JSONLoader(new InternalFileHandleResolver()));

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
		this.settings = new GameSettings(
				NAME.trim().replace(" ", "-").toLowerCase());

		// Create the input multiplexer
		this.inputProcessor = new GameInputMultiplexer(this);

		// Create the event bus
		this.eventBus = new EventQueueBus();

		// Set input processor
		Gdx.input.setInputProcessor(inputProcessor);

		// Add screens
		addScreen("credits", new CreditsScreen());
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
		addScreen("settings", new SettingsScreen());
		addScreen("vote", new GameVoteScreen());

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

	@Override
	protected void onScreenInitialization(BaseScreen screen) {
		screen.init(this);
	}

	@Override
	public final void dispose() {
		super.dispose();

		this.batch.dispose();
		if (uiSkin != null)
			this.uiSkin.dispose();
	}

	/**
	 * @return the camera used in the actual game.
	 */
	public CameraWrapper getGameCamera() {
		return this.gameCamera;
	}

	/**
	 * @return the camera used by the UI screens.
	 * @see BaseUIScreen#render(float)
	 */
	public Camera getUICamera() {
		return this.uiCamera;
	}

	/**
	 * @return an instance of the game settings handler.
	 */
	public GameSettings getSettings() {
		return settings;
	}

	/**
	 * @return the events bus. See {@link EventQueueBus}. Events are processed
	 *         in the rendering thread.
	 */
	public EventQueueBus getEventBus() {
		return eventBus;
	}

	/**
	 * Sets the UI skin.
	 *
	 * @param skin
	 *            the UI skin.
	 */
	public void setUISkin(Skin skin) {
		this.uiSkin = skin;
	}

	/**
	 * @return the skin for the game's UI elements. Got loaded in the
	 *         {@link LoadingScreen}.
	 */
	public Skin getUISkin() {
		return uiSkin;
	}

	/**
	 * @return the sprite batch to render 2D stuff with.
	 */
	public SpriteBatch getSpriteBatch() {
		return batch;
	}

	/**
	 * @return the game client. <code>Null</code> if the player is not in a game
	 *         or currently disconnecting from one.
	 */
	public GameClient getClient() {
		return client;
	}

	public void setClient(GameClient client) {
		this.client = client;
	}

	/**
	 * @return the game server. Is <code>null</code> if the player is not
	 *         hosting a game.
	 */
	public GameServer getServer() {
		return server;
	}

	public void setServer(GameServer server) {
		this.server = server;
	}

	public boolean isHost() {
		return server != null;
	}

	/**
	 * Returns the input multiplexer of the game. Should be used to add input
	 * listeners instead of {@link Input#setInputProcessor(InputProcessor)}.
	 *
	 * @return the game's input multiplexer.
	 */
	public GameInputMultiplexer getInputMultiplexer() {
		return inputProcessor;
	}

	/**
	 * @return whether the debug flag is set and thus debug stuff should get
	 *         rendered.
	 */
	public boolean showDebugStuff() {
		return debug;
	}

	/**
	 * @return whether a fps counter should get shown.
	 */
	public boolean showFPSCounter() {
		return fpsCounter;
	}

	public void setFPSCounter(boolean fpsCounter) {
		this.fpsCounter = fpsCounter;
	}

}
