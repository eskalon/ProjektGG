package de.gg.game.ui.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.google.common.eventbus.Subscribe;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.log.Log;
import de.eskalon.commons.screens.AbstractAssetLoadingScreen;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.events.ServerReadyEvent;
import de.gg.game.model.World;
import de.gg.game.model.entities.BuildingSlot;
import de.gg.game.model.types.BuildingType;
import de.gg.game.session.GameSession;
import de.gg.game.ui.rendering.SelectableRenderData;

/**
 * This screen takes care of loading the assets for all in-game screens.
 */
public class GameLoadingScreen extends AbstractAssetLoadingScreen {

	@Asset("ui/backgrounds/game_loading_screen.jpg")
	private Texture backgroundTexture;
	@Asset("ui/loading_bar_top.png")
	private Texture topBarTexture;
	@Asset("ui/loading_bar_bottom.png")
	private Texture bottomBarTexture;

	private static final Vector3 Y_AXIS = new Vector3(0, 1, 0);

	public GameLoadingScreen(EskalonApplication application) {
		super(application, null);
	}

	@Override
	public void show() {
		super.show();

		// Load game assets
		application.getAssetManager().load(
				((ProjektGGApplication) application).getClient().getSession()
						.getSessionSetup().getMap().getSkyboxPath(),
				Model.class);

		for (BuildingType t : BuildingType.values()) {
			application.getAssetManager().load(t.getModelPath(), Model.class);
		}

		// TODO unload them when disconnecting?
	}

	@Override
	protected void onFinishedLoading() {
		GameSession session = ((ProjektGGApplication) application).getClient()
				.getSession();
		World world = session.getWorld();

		// Create the ModelInstances
		for (BuildingSlot s : world.getBuildingSlots()) {
			if (s.isBuiltOn()) {
				s.getBuilding().setRenderData(
						new SelectableRenderData(application.getAssetManager()
								.get(s.getBuilding().getType().getModelPath(),
										Model.class)));
				s.getBuilding().getRenderData().transform.translate(s.getPosX(),
						0, s.getPosZ());
				s.getBuilding().getRenderData().transform.rotate(Y_AXIS,
						s.getRotationToStreet());

				// TODO Use _one_ scene for all models?
				// new SelectableRenderData(scene,
				// building1.getType().getNodeName(), true);
			}
		}

		world.setSkybox(new ModelInstance(application.getAssetManager().get(
				session.getSessionSetup().getMap().getSkyboxPath(),
				Model.class)));

		Log.info("Client", "Game assets loaded");

		if (((ProjektGGApplication) application).isHost()) {
			((ProjektGGApplication) application).getServer().startMatch();
			Log.info("Server", "Game started!");
		}

		application.getScreenManager().pushScreen("round_end", "simple_zoom");
	}

	@Subscribe
	public void onRoundEndDataArrived(ServerReadyEvent event) {
		((GameRoundendScreen) application.getScreenManager()
				.getScreen("round_end")).setServerReady();
	}

	@Override
	public void render(float delta, float progress) {
		application.getSpriteBatch().begin();
		application.getSpriteBatch()
				.setProjectionMatrix(application.getUICamera().combined);

		// Draw the background
		application.getSpriteBatch().draw(this.backgroundTexture, 0, 0,
				application.getWidth(), application.getHeight());

		// Get useful values
		float imageWidth = topBarTexture.getWidth();
		float imageHeight = topBarTexture.getHeight();

		// The actual drawing
		application.getSpriteBatch().draw(bottomBarTexture,
				(application.getWidth() / 2) - (imageWidth / 2) + 1,
				(application.getHeight() / 4) - imageHeight / 2);
		application.getSpriteBatch().draw(topBarTexture,
				(application.getWidth() / 2) - (imageWidth / 2),
				(application.getHeight() / 4) - imageHeight / 2, 0, 0,
				Math.round(imageWidth * progress), (int) imageHeight);

		application.getSpriteBatch().end();
	}

	@Override
	protected void loadOwnAssets() {
		// not needed as screen assets are loaded beforehand
	}

	@Override
	public void dispose() {
		// nothing to dispose that isn't disposed elsewhere
	}

}
