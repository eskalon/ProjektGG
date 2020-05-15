package de.gg.game.core;

import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.annotation.Nullable;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.reflect.TypeToken;

import de.eskalon.commons.asset.AnnotationAssetManager.AssetLoaderParametersFactory;
import de.eskalon.commons.asset.JSON;
import de.eskalon.commons.asset.JSONLoader.JSONLoaderParameter;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.core.EskalonSplashScreen;
import de.eskalon.commons.screen.transition.impl.BlankTimedTransition;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import de.eskalon.commons.screens.AbstractAssetLoadingScreen;
import de.eskalon.commons.screens.BlankEskalonScreen;
import de.gg.game.misc.PlayerUtils.PlayerStub;
import de.gg.game.network.GameClient;
import de.gg.game.network.GameServer;
import de.gg.game.ui.screens.AssetLoadingScreen;
import de.gg.game.ui.screens.CreditsScreen;
import de.gg.game.ui.screens.GameBallotScreen;
import de.gg.game.ui.screens.GameLoadingScreen;
import de.gg.game.ui.screens.GameMapScreen;
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

	public static final String NAME = "ProjektGG";

	private Skin uiSkin;

	private GameSettings settings;

	@Nullable
	private GameServer server;
	@Nullable
	private GameClient client;

	@Override
	public void create() {
		super.create();

		// Asset loading
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

		// Settings
		this.settings = new GameSettings(
				NAME.trim().replace(" ", "-").toLowerCase());
		this.soundManager.setEffectVolume(settings.getEffectVolume());
		this.soundManager.setMasterVolume(settings.getMasterVolume());
		this.soundManager.setMusicVolume(settings.getMusicVolume());

		// Add screens
		this.screenManager.addScreen("blank", new BlankEskalonScreen(this));
		screenManager.addScreen("credits", new CreditsScreen(this));
		screenManager.addScreen("splash",
				new EskalonSplashScreen(this, (param) -> {
					screenManager.pushScreen("blank", "splashOutTransition1");
					screenManager.pushScreen("blank", "splashOutTransition2");
					screenManager.pushScreen("loading", "splashOutTransition3");
				}));
		screenManager.addScreen("main_menu", new MainMenuScreen(this));
		screenManager.addScreen("loading", new AssetLoadingScreen(this));
		screenManager.addScreen("game_loading", new GameLoadingScreen(this));
		screenManager.addScreen("server_browser",
				new ServerBrowserScreen(this));
		screenManager.addScreen("lobby", new LobbyScreen(this));
		screenManager.addScreen("lobbyCreation", new LobbyCreationScreen(this));
		screenManager.addScreen("map", new GameMapScreen(this));
		screenManager.addScreen("house_town_hall",
				new GameTownHallInteriorScreen(this));
		screenManager.addScreen("round_end", new GameRoundendScreen(this));
		screenManager.addScreen("settings", new SettingsScreen(this));
		screenManager.addScreen("vote", new GameBallotScreen(this));

		// Splash screen transitions
		BlendingTransition splashBlendingTransition = new BlendingTransition(
				batch, 0.25F, Interpolation.exp10In);
		screenManager.addScreenTransition("splashInTransition",
				splashBlendingTransition);
		BlendingTransition splashOutTransition1 = new BlendingTransition(batch,
				0.18F, Interpolation.exp5Out);
		screenManager.addScreenTransition("splashOutTransition1",
				splashOutTransition1);
		BlankTimedTransition splashOutTransition2 = new BlankTimedTransition(
				0.22F);
		screenManager.addScreenTransition("splashOutTransition2",
				splashOutTransition2);
		BlendingTransition splashOutTransition3 = new BlendingTransition(batch,
				0.275F, Interpolation.pow2In);
		screenManager.addScreenTransition("splashOutTransition3",
				splashOutTransition3);

		// Other transitions
		BlendingTransition assetLoadingTransition = new BlendingTransition(
				batch, 0.51F, Interpolation.pow2In);
		screenManager.addScreenTransition("assetLoadingTransition",
				assetLoadingTransition);

		// Push the first screen
		screenManager.pushScreen("splash", "splashInTransition");
	}

	public GameSettings getSettings() {
		return settings;
	}

	public void setUISkin(Skin skin) {
		this.uiSkin = skin;
	}

	public Skin getUISkin() {
		return uiSkin;
	}

	/**
	 * @return the game client; {@code null} if the player is not in a game or
	 *         currently disconnecting from one
	 */
	@Nullable
	public GameClient getClient() {
		return client;
	}

	public void setClient(@Nullable GameClient client) {
		this.client = client;
	}

	/**
	 * @return the game server; {@code null} if the player is not hosting a game
	 */
	@Nullable
	public GameServer getServer() {
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
		super.dispose();
		if (uiSkin != null)
			uiSkin.dispose();
	}

}