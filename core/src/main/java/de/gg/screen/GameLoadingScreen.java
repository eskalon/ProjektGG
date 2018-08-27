package de.gg.screen;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.google.common.eventbus.Subscribe;

import de.gg.event.ServerReadyEvent;
import de.gg.game.entity.BuildingSlot;
import de.gg.render.RenderData;
import de.gg.util.Log;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

/**
 * This screen takes care of loading the assets for all ingame screens.
 */
public class GameLoadingScreen extends BaseLoadingScreen {

	private static final String HOUSE1_MODEL_PATH = "models/buildings/test_houses/house1.g3db";
	private static final String SKYBOX_MODEL_PATH = "models/skybox/skybox.g3db";
	private static final Vector3 Y_AXIS = new Vector3(0, 1, 0);

	public GameLoadingScreen() {
		super("ui/backgrounds/altar.jpg");
	}

	@Override
	protected void initAssets(AnnotationAssetManager assetManager) {
		assetManager.load(game.getScreen("map"));
		assetManager.load(game.getScreen("house"));
		assetManager.load(game.getScreen("roundEnd"));
		assetManager.load(game.getScreen("vote"));

		// Load all models
		// TODO replace with one big scene
		assetManager.load(HOUSE1_MODEL_PATH, Model.class);
		assetManager.load(SKYBOX_MODEL_PATH, Model.class);
	}

	@Override
	protected synchronized void onFinishedLoading(
			AnnotationAssetManager assetManager) {
		game.getScreen("map").finishLoading(assetManager);
		game.getScreen("house").finishLoading(assetManager);
		game.getScreen("roundEnd").finishLoading(assetManager);
		game.getScreen("vote").finishLoading(assetManager);

		// TODO folgendes als Worker, der in eigenen Loading-Screen integriert
		// ist, umsetzen, sodass sich für den Nutzer sichtbar ein Balken bewegt

		// Set up the game (server and client side)
		if (game.isHost())
			game.getServer().initGameSession();
		game.getClient().initGameSession();

		// Create the ModelInstances
		for (BuildingSlot s : game.getClient().getCity().getBuildingSlots()) {
			if (s.isBuiltOn()) {
				s.getBuilding().setRenderData(new RenderData(
						assetManager.get(HOUSE1_MODEL_PATH, Model.class)));
				s.getBuilding().getRenderData().transform.translate(s.getPosX(),
						0, s.getPosY() /* umbenennen in getPosZ */);
				s.getBuilding().getRenderData().transform.rotate(Y_AXIS,
						s.getRotationToStreet());

				// TODO Use _one_ scene for all models:
				// new RenderData(scene, building1.getType().getNodeName(),
				// true);

			}
		}
		game.getClient().getCity().setSkybox(new ModelInstance(
				assetManager.get(SKYBOX_MODEL_PATH, Model.class)));

		Log.info("Client", "Spiel geladen");

		if (game.isHost()) {
			game.getServer().startGame();
			Log.info("Server", "Spiel gestartet");
		}

		game.pushScreen("roundEnd");
	}

	@Subscribe
	public void onRoundEndDataArrived(ServerReadyEvent event) {
		((GameRoundendScreen) game.getScreen("roundEnd")).setServerReady();
	}

}
