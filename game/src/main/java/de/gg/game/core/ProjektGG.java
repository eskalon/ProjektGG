package de.gg.game.core;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetLoaderParameters;
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
import com.google.common.reflect.TypeToken;

import de.gg.engine.asset.AnnotationAssetManager.AssetLoaderParametersFactory;
import de.gg.engine.asset.BitmapFontAssetLoaderParametersFactory;
import de.gg.engine.asset.JSON;
import de.gg.engine.asset.JSONLoader;
import de.gg.engine.asset.JSONLoader.JSONLoaderParameter;
import de.gg.engine.asset.Text;
import de.gg.engine.asset.TextLoader;
import de.gg.engine.core.BaseGame;
import de.gg.engine.ui.rendering.CameraWrapper;
import de.gg.engine.ui.screens.BaseScreen;
import de.gg.game.input.ProjektGGInputMultiplexer;
import de.gg.game.network.GameClient;
import de.gg.game.network.GameServer;
import de.gg.game.ui.screens.BaseUIScreen;
import de.gg.game.ui.screens.CreditsScreen;
import de.gg.game.ui.screens.GameInHouseScreen;
import de.gg.game.ui.screens.GameLoadingScreen;
import de.gg.game.ui.screens.GameMapScreen;
import de.gg.game.ui.screens.GameRoundendScreen;
import de.gg.game.ui.screens.GameVoteScreen;
import de.gg.game.ui.screens.LoadingScreen;
import de.gg.game.ui.screens.LobbyCreationScreen;
import de.gg.game.ui.screens.LobbyScreen;
import de.gg.game.ui.screens.MainMenuScreen;
import de.gg.game.ui.screens.ServerBrowserScreen;
import de.gg.game.ui.screens.SettingsScreen;
import de.gg.game.ui.screens.SplashScreen;
import de.gg.game.utils.PlayerUtils.PlayerStub;

/**
 * This class starts the game by creating all the necessary screens and then
 * displaying the {@link SplashScreen} or the {@link LoadingScreen} depending on
 * the {@link #showSplashscreen}-flag.
 * <p>
 * The assets of the screens are loaded when the {@link LoadingScreen} is shown.
 * <p>
 * Only {@link BaseScreen}s are supported.
 */
public class ProjektGG extends BaseGame {
	public static final String NAME = "ProjektGG";

	private SpriteBatch batch;

	private OrthographicCamera uiCamera;
	private CameraWrapper gameCamera;

	private boolean showSplashscreen, fpsCounter;

	private Skin uiSkin;

	private GameServer server;
	private GameClient client;

	public ProjektGG(boolean debug, boolean showSplashscreen,
			boolean fpsCounter) {
		super(NAME.trim().replace(" ", "-").toLowerCase(), debug);

		this.showSplashscreen = showSplashscreen;
		this.fpsCounter = fpsCounter;
	}

	@Override
	protected void onGameInitialization() {
		setInputMultiplexer(new ProjektGGInputMultiplexer(this));

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
		this.assetManager.registerAssetLoaderParametersFactory(BitmapFont.class,
				new BitmapFontAssetLoaderParametersFactory());
		this.assetManager.registerAssetLoaderParametersFactory(JSON.class,
				new AssetLoaderParametersFactory<JSON>() {
					private Type stringListType = new TypeToken<ArrayList<String>>() {
					}.getType();
					private Type stubListType = new TypeToken<ArrayList<PlayerStub>>() {
					}.getType();

					@Override
					public AssetLoaderParameters<JSON> newInstance(String path,
							String params) {
						if (params.equals("array_list_string"))
							return new JSONLoaderParameter(stringListType);
						if (params.equals("array_list_player_stub"))
							return new JSONLoaderParameter(stubListType);

						return null;
					}
				});

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
	 * @return whether a fps counter should get shown.
	 */
	public boolean showFPSCounter() {
		return fpsCounter;
	}

	public void setFPSCounter(boolean fpsCounter) {
		this.fpsCounter = fpsCounter;
	}

}
