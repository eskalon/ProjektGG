package de.eskalon.gg.core;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.math.Interpolation;
import com.google.gson.reflect.TypeToken;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.asset.AnnotationAssetManager.AssetLoaderParametersFactory;
import de.eskalon.commons.core.AbstractEskalonApplication;
import de.eskalon.commons.core.EskalonApplicationConfiguration;
import de.eskalon.commons.core.EskalonApplicationContext;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.IInjector;
import de.eskalon.commons.input.DefaultInputHandler;
import de.eskalon.commons.input.IInputHandler;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import de.eskalon.commons.screens.AbstractAssetLoadingScreen;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.gg.asset.JSON;
import de.eskalon.gg.asset.JSONAssetProvider;
import de.eskalon.gg.asset.JSONLoader;
import de.eskalon.gg.asset.JSONLoader.JSONLoaderParameter;
import de.eskalon.gg.graphics.rendering.GameRenderer;
import de.eskalon.gg.misc.ImGuiRenderer;
import de.eskalon.gg.misc.PlayerUtils.PlayerTemplate;
import de.eskalon.gg.net.GameClient;
import de.eskalon.gg.screens.AssetLoadingScreen;
import de.eskalon.gg.screens.CreditsScreen;
import de.eskalon.gg.screens.LobbyCreationScreen;
import de.eskalon.gg.screens.LobbyScreen;
import de.eskalon.gg.screens.MainMenuScreen;
import de.eskalon.gg.screens.ServerBrowserScreen;
import de.eskalon.gg.screens.SettingsScreen;
import de.eskalon.gg.screens.game.GameLoadingScreen;
import de.eskalon.gg.screens.game.MapScreen;
import de.eskalon.gg.screens.game.MapScreen.GameMapAxisBinding;
import de.eskalon.gg.screens.game.MapScreen.GameMapBinaryBinding;
import de.eskalon.gg.screens.game.RoundEndScreen;
import de.eskalon.gg.screens.game.VoteScreen;
import de.eskalon.gg.screens.game.house.TownHallInteriorScreen;
import de.eskalon.gg.simulation.GameClock;
import de.eskalon.gg.simulation.GameHandler;

/**
 * This is the main game class. The game uses an
 * {@link AbstractAssetLoadingScreen} to load all annotated assets.
 */
public class ProjektGGApplication extends AbstractEskalonApplication {

	public static final String GAME_NAME = "ProjektGG";

	public ProjektGGApplication() {
		super(EskalonApplicationConfiguration.create().provideDepthBuffers()
				.createPostProcessor().build());
	}

	@Override
	public Class<? extends AbstractEskalonScreen> initApp() {
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
		IInjector injector = EskalonInjector.instance();
		injector.bindToQualifiedProvider(JSON.class, Asset.class,
				JSONAssetProvider.class);

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

		// Register screens
		injector.bindToConstructor(CreditsScreen.class);
		injector.bindToConstructor(MainMenuScreen.class);
		injector.bindToConstructor(AssetLoadingScreen.class);
		injector.bindToConstructor(GameLoadingScreen.class);
		injector.bindToConstructor(ServerBrowserScreen.class);
		injector.bindToConstructor(LobbyScreen.class);
		injector.bindToConstructor(LobbyCreationScreen.class);
		injector.bindToConstructor(MapScreen.class);
		injector.bindToConstructor(TownHallInteriorScreen.class);
		injector.bindToConstructor(RoundEndScreen.class);
		injector.bindToConstructor(SettingsScreen.class);
		injector.bindToConstructor(VoteScreen.class);

		// Register basic transitions
		BlendingTransition shortBlendingTransition = new BlendingTransition(
				batch, 0.15F, Interpolation.pow2In);
		appContext.getTransitions().put("shortBlendingTransition",
				shortBlendingTransition);
		BlendingTransition blendingTransition = new BlendingTransition(batch,
				0.39F, Interpolation.sine);
		appContext.getTransitions().put("blendingTransition",
				blendingTransition);
		BlendingTransition longBlendingTransition = new BlendingTransition(
				batch, 0.51F, Interpolation.pow2In);
		appContext.getTransitions().put("longBlendingTransition",
				longBlendingTransition);

		// Register basic game stuff
		injector.bindToConstructor(GameClient.class);
		injector.bindToConstructor(GameHandler.class);
		injector.bindToConstructor(GameRenderer.class);
		injector.bindToConstructor(GameClock.class);

		// Use ProjektGG context
		this.appContext = new ProjektGGApplicationContext(appContext);
		injector.bindToInstance(EskalonApplicationContext.class, null); // not
																		// needed,
																		// but
																		// better
																		// safe
																		// than
																		// sorry
		injector.bindToInstance(ProjektGGApplicationContext.class,
				(ProjektGGApplicationContext) appContext);
		injector.bindToSubclass(EskalonApplicationContext.class,
				ProjektGGApplicationContext.class);

		// ImGui
		ImGuiRenderer.init();

		return AssetLoadingScreen.class;
	}

	@Override
	public void dispose() {
		ImGuiRenderer.dispose();

		if (((ProjektGGApplicationContext) appContext).getClient() != null)
			((ProjektGGApplicationContext) appContext).getClient().stop();

		if (((ProjektGGApplicationContext) appContext).isHost())
			((ProjektGGApplicationContext) appContext).getServer().stop();

		if (((ProjektGGApplicationContext) appContext).getGameHandler() != null)
			((ProjektGGApplicationContext) appContext).getGameHandler()
					.dispose();

		if (((ProjektGGApplicationContext) appContext)
				.getGameRenderer() != null)
			((ProjektGGApplicationContext) appContext).getGameRenderer()
					.dispose();

		super.dispose();
	}

}
