package de.gg.screen;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import de.gg.entity.BuildingSlot;
import de.gg.entity.BuildingTypes;
import de.gg.entity.ItemTypes;
import de.gg.entity.PositionTypes;
import de.gg.entity.ProfessionTypes;
import de.gg.entity.SocialStatusS;
import de.gg.render.RenderData;
import de.gg.render.TestShader;
import de.gg.util.Log;

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
	protected void initAssets() {
		assetManager.load(TestShader.class);
		assetManager.load(BuildingTypes.class);
		assetManager.load(PositionTypes.class);
		assetManager.load(ProfessionTypes.class);
		assetManager.load(SocialStatusS.class);
		assetManager.load(ItemTypes.class);
		assetManager.load(ItemTypes.class);

		assetManager.load(game.getScreen("map"));
		assetManager.load(game.getScreen("house"));
		assetManager.load(game.getScreen("roundEnd"));

		// Load all models
		// TODO replace with one big scene
		assetManager.load(HOUSE1_MODEL_PATH, Model.class);
		assetManager.load(SKYBOX_MODEL_PATH, Model.class);

	}

	@Override
	protected synchronized void onFinishedLoading() {
		game.getScreen("map").finishLoading();
		game.getScreen("house").finishLoading();
		game.getScreen("roundEnd").finishLoading();
		BuildingTypes.finishLoading(assetManager);
		PositionTypes.finishLoading(assetManager);
		ProfessionTypes.finishLoading(assetManager);
		SocialStatusS.finishLoading(assetManager);
		ItemTypes.finishLoading(assetManager);
		ItemTypes.finishLoading(assetManager);

		// TODO folgendes als Worker, der in eigenen Loading-Screen integriert
		// ist, umsetzen, sodass sich für den Nutzer sichtbar ein Balken bewegt

		// Das Spiel aufsetzen
		game.getCurrentSession().setupGame();
		// Die ModelInstances erstellen
		for (BuildingSlot s : game.getCurrentSession().getCity()
				.getBuildingSlots()) {
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
		game.getCurrentSession().getCity().setSkybox(new ModelInstance(
				assetManager.get(SKYBOX_MODEL_PATH, Model.class)));

		// Screen wechseln
		Log.info("Client", "Spiel gestartet");
		game.pushScreen("roundEnd");
	}

}
