package de.gg.game.core;

import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.annotation.Nullable;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.math.Interpolation;
import com.google.gson.reflect.TypeToken;

import de.damios.guacamole.concurrent.ThreadHandler;
import de.eskalon.commons.asset.AnnotationAssetManager.AssetLoaderParametersFactory;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.core.EskalonApplicationConfiguration;
import de.eskalon.commons.input.DefaultInputHandler;
import de.eskalon.commons.input.IInputHandler;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import de.eskalon.commons.screens.AbstractAssetLoadingScreen;
import de.eskalon.commons.screens.EskalonSplashScreen;
import de.gg.game.asset.JSON;
import de.gg.game.asset.JSONLoader;
import de.gg.game.asset.JSONLoader.JSONLoaderParameter;
import de.gg.game.misc.PlayerUtils.PlayerTemplate;
import de.gg.game.network.GameClient;
import de.gg.game.network.GameServer;
import de.gg.game.ui.screens.AssetLoadingScreen;
import de.gg.game.ui.screens.CreditsScreen;
import de.gg.game.ui.screens.GameBallotScreen;
import de.gg.game.ui.screens.GameLoadingScreen;
import de.gg.game.ui.screens.GameMapScreen;
import de.gg.game.ui.screens.GameMapScreen.GameMapAxisBinding;
import de.gg.game.ui.screens.GameMapScreen.GameMapBinaryBinding;
import de.gg.game.ui.screens.GameRoundendScreen;
import de.gg.game.ui.screens.GameTownHallInteriorScreen;
import de.gg.game.ui.screens.LobbyCreationScreen;
import de.gg.game.ui.screens.LobbyScreen;
import de.gg.game.ui.screens.MainMenuScreen;
import de.gg.game.ui.screens.ServerBrowserScreen;
import de.gg.game.ui.screens.SettingsScreen;

/**
 * This class starts the game by creating all the necessary screens and then
 * displaying the {@link EskalonSplashScreen}.
 * <p>
 * Uses an {@link AbstractAssetLoadingScreen} to load all annotated assets.
 */
public class ProjektGGApplication extends EskalonApplication {

	public static final String GAME_NAME = "ProjektGG";

	private @Nullable GameServer server;
	private @Nullable GameClient client;

	@Override
	protected EskalonApplicationConfiguration getAppConfig() {
		return super.getAppConfig().setAppName(GAME_NAME).provideDepthBuffers();
	}

	@Override
	public String initApp() {
		// Asset loading
		this.assetManager.setLoader(JSON.class,
				new JSONLoader(this.assetManager.getFileHandleResolver()));
		this.assetManager.registerAssetLoaderParametersFactory(JSON.class,
				new AssetLoaderParametersFactory<JSON>() {
					private Type stringListType = new TypeToken<ArrayList<String>>() {
					}.getType();
					private Type stubListType = new TypeToken<ArrayList<PlayerTemplate>>() {
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

		// Default keybinds
		IInputHandler.registerAxisBinding(settings,
				GameMapAxisBinding.MOVE_LEFT_RIGHT, Keys.A, Keys.D, -2);
		IInputHandler.registerAxisBinding(settings,
				GameMapAxisBinding.MOVE_FORWARDS_BACKWARDS, Keys.S, Keys.W, -2);
		IInputHandler.registerAxisBinding(settings, GameMapAxisBinding.ZOOM, -2,
				-2, DefaultInputHandler.SCROLL_AXIS_Y);

		IInputHandler.registerBinaryBinding(settings,
				GameMapBinaryBinding.INCREASE_SPEED, Keys.NUMPAD_ADD, -2,
				false);
		IInputHandler.registerBinaryBinding(settings,
				GameMapBinaryBinding.DECREASE_SPEED, Keys.NUMPAD_SUBTRACT, -2,
				false);
		IInputHandler.registerBinaryBinding(settings,
				GameMapBinaryBinding.ROTATE_CAMERA_BUTTON, -2, Buttons.RIGHT,
				false);
		IInputHandler.registerBinaryBinding(settings,
				GameMapBinaryBinding.SELECT_BUILDING, -2, Buttons.LEFT, false);

		// Add screens
		screenManager.addScreen("credits", new CreditsScreen(this));
		screenManager.addScreen("main_menu", new MainMenuScreen(this));
		screenManager.addScreen("loading", new AssetLoadingScreen(this));
		screenManager.addScreen("game_loading", new GameLoadingScreen(this));
		screenManager.addScreen("server_browser",
				new ServerBrowserScreen(this));
		screenManager.addScreen("lobby", new LobbyScreen(this));
		screenManager.addScreen("lobby_creation",
				new LobbyCreationScreen(this));
		screenManager.addScreen("map", new GameMapScreen(this));
		screenManager.addScreen("house_town_hall",
				new GameTownHallInteriorScreen(this));
		screenManager.addScreen("round_end", new GameRoundendScreen(this));
		screenManager.addScreen("settings", new SettingsScreen(this));
		screenManager.addScreen("vote", new GameBallotScreen(this));

		// Transitions
		BlendingTransition shortBlendingTransition = new BlendingTransition(
				batch, 0.15F, Interpolation.pow2In);
		screenManager.addScreenTransition("shortBlendingTransition",
				shortBlendingTransition);
		BlendingTransition blendingTransition = new BlendingTransition(batch,
				0.39F, Interpolation.sine);
		screenManager.addScreenTransition("blendingTransition",
				blendingTransition);
		BlendingTransition longBlendingTransition = new BlendingTransition(
				batch, 0.51F, Interpolation.pow2In);
		screenManager.addScreenTransition("longBlendingTransition",
				longBlendingTransition);

		return "loading";
	}

	/**
	 * @return the game client; {@code null} if the player is not in a game or
	 *         currently disconnecting from one
	 */
	public @Nullable GameClient getClient() {
		return client;
	}

	public void setClient(@Nullable GameClient client) {
		this.client = client;
	}

	/**
	 * @return the game server; {@code null} if the player is not hosting a game
	 */
	public @Nullable GameServer getServer() {
		return server;
	}

	public void setServer(@Nullable GameServer server) {
		this.server = server;
	}

	public boolean isHost() {
		return server != null;
	}

	@Override
	public void dispose() {
		if (client != null)
			client.disconnect();

		if (isHost())
			ThreadHandler.getInstance().executeRunnable(() -> server.stop());

		super.dispose();
	}

}
