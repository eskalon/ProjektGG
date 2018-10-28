package de.gg.game.ui.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.google.common.eventbus.Subscribe;

import de.gg.engine.asset.AnnotationAssetManager;
import de.gg.engine.asset.AnnotationAssetManager.InjectAsset;
import de.gg.engine.log.Log;
import de.gg.game.entities.BuildingSlot;
import de.gg.game.events.ServerReadyEvent;
import de.gg.game.ui.rendering.SelectableRenderData;
import de.gg.game.world.World;

/**
 * This screen takes care of loading the assets for all ingame screens.
 */
public class GameLoadingScreen extends BaseLoadingScreen {

	@InjectAsset("ui/backgrounds/altar.jpg")
	private Texture backgroundImage;

	private static final String HOUSE1_MODEL_PATH = "models/buildings/test_houses/house1.g3db";
	private static final String SKYBOX_MODEL_PATH = "models/skybox/skybox.g3db";

	private static final Vector3 Y_AXIS = new Vector3(0, 1, 0);

	@Override
	protected void initAssets(AnnotationAssetManager assetManager) {
		super.backgroundTexture = backgroundImage;
		assetManager.load(GameMapScreen.class);
		assetManager.load(GameInHouseScreen.class);
		assetManager.load(GameRoundendScreen.class);
		assetManager.load(GameVoteScreen.class);

		// Load all models
		// TODO replace with one big scene
		assetManager.load(HOUSE1_MODEL_PATH, Model.class);
		assetManager.load(SKYBOX_MODEL_PATH, Model.class);
	}

	@Override
	protected synchronized void onFinishedLoading(
			AnnotationAssetManager assetManager) {
		assetManager.injectAssets(game.getScreen("map"));
		assetManager.injectAssets(game.getScreen("house"));
		assetManager.injectAssets(game.getScreen("roundEnd"));
		assetManager.injectAssets(game.getScreen("vote"));

		game.getScreen("map").finishLoading();
		game.getScreen("house").finishLoading();
		game.getScreen("roundEnd").finishLoading();
		game.getScreen("vote").finishLoading();

		World world = game.getClient().getSession().getWorld();

		// TODO folgendes als Worker, der in eigenen Loading-Screen integriert
		// ist, umsetzen, sodass sich für den Nutzer sichtbar ein Balken bewegt

		// Create the ModelInstances
		for (BuildingSlot s : world.getBuildingSlots()) {
			if (s.isBuiltOn()) {
				s.getBuilding().setRenderData(new SelectableRenderData(
						assetManager.get(HOUSE1_MODEL_PATH, Model.class)));
				s.getBuilding().getRenderData().transform.translate(s.getPosX(),
						0, s.getPosZ());
				s.getBuilding().getRenderData().transform.rotate(Y_AXIS,
						s.getRotationToStreet());

				// TODO Use _one_ scene for all models:
				// new SelectableRenderData(scene,
				// building1.getType().getNodeName(), true);

			}
		}
		world.setSkybox(new ModelInstance(
				assetManager.get(SKYBOX_MODEL_PATH, Model.class)));

		Log.info("Client", "Assets geladen");

		if (game.isHost()) {
			game.getServer().startMatch();
			Log.info("Server", "Match gestartet");
		}

		game.pushScreen("roundEnd");
	}

	@Subscribe
	public void onRoundEndDataArrived(ServerReadyEvent event) {
		((GameRoundendScreen) game.getScreen("roundEnd")).setServerReady();
	}

}
