package de.eskalon.gg.screens.game;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.screens.AbstractEskalonUIScreen;
import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.gg.core.ProjektGGApplicationContext;
import de.eskalon.gg.graphics.rendering.GameRenderer;
import de.eskalon.gg.graphics.rendering.SelectableRenderData;
import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.entities.BuildingSlot;

/**
 * This screen takes care of initializing the game world and the 3D scene.
 */
public class GameLoadingScreen extends AbstractEskalonUIScreen {

	private static final Logger LOG = LoggerService
			.getLogger(GameLoadingScreen.class);

	private @Inject AssetManager assetManager;
	private @Inject EskalonScreenManager screenManager;
	private @Inject ProjektGGApplicationContext appContext;

	@Asset("ui/backgrounds/game_loading_screen.jpg")
	private @Inject Texture backgroundTexture;
	@Asset("ui/loading_bar_top.png")
	private @Inject Texture topBarTexture;
	@Asset("ui/loading_bar_bottom.png")
	private @Inject Texture bottomBarTexture;

	private Viewport viewport = new ScreenViewport();

	private static final Vector3 Y_AXIS = new Vector3(0, 1, 0);

	private Queue<Runnable> taskQueue = new LinkedList<>();
	private int taskCount;
	private boolean once = false;

	@Override
	public void show() {
		super.show();

		World world = appContext.getGameHandler().getSimulation().getWorld();

		// Create ModelInstances for the buildings
		for (BuildingSlot s : world.getBuildingSlots()) {
			if (s.isBuiltOn()) {
				taskQueue.add(() -> {
					s.getBuilding().setRenderData(
							new SelectableRenderData(assetManager.get(
									s.getBuilding().getType().getModelPath(),
									Model.class)));
					s.getBuilding().getRenderData().transform
							.translate(s.getPosX(), 0, s.getPosZ());
					s.getBuilding().getRenderData().transform.rotate(Y_AXIS,
							s.getRotationToStreet());

					// TODO Use _one_ scene for all models?
					// new SelectableRenderData(scene,
					// building1.getType().getNodeName(), true);
				});

			}
		}
		// Skybox
		taskQueue.add(() -> {
			Model skyboxModel = assetManager.get(appContext.getGameHandler()
					.getSimulation().getWorld().getMap().getSkyboxPath(),
					Model.class);
			for (Material m : skyboxModel.materials) {
				// Fixes a bug related to changes in gdx 1.9.9, see
				// https://github.com/libgdx/libgdx/issues/5529
				m.remove(ColorAttribute.Emissive);
			}
			world.setSkybox(new ModelInstance(skyboxModel));
		});

		// Final task: create the game renderer
		taskQueue.add(() -> {
			appContext.setGameRenderer(
					EskalonInjector.instance().getInstance(GameRenderer.class));
			appContext.getGameRenderer().init();
		});

		taskCount = taskQueue.size();
	}

	@Override
	public void render(float delta) {
		viewport.apply();
		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.begin();

		// Execute stuff
		Runnable task;
		if ((task = taskQueue.poll()) != null) {
			// TODO: this only executes one task per frame!
			task.run();
		} else if (!once) {
			once = true;
			LOG.info("[CLIENT] Game loading finished");
			screenManager.pushScreen(RoundEndScreen.class, "simple_zoom");
		}

		float progress = Math.round(taskCount * 100F / taskQueue.size());

		// Draw the background
		batch.draw(this.backgroundTexture, 0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		// The actual drawing
		batch.draw(bottomBarTexture,
				(Gdx.graphics.getWidth() / 2) - (topBarTexture.getWidth() / 2)
						+ 1,
				(Gdx.graphics.getHeight() / 4) - topBarTexture.getHeight() / 2);
		batch.draw(topBarTexture,
				(Gdx.graphics.getWidth() / 2) - (topBarTexture.getWidth() / 2),
				(Gdx.graphics.getHeight() / 4) - topBarTexture.getHeight() / 2,
				0, 0, Math.round(topBarTexture.getWidth() * progress),
				(int) topBarTexture.getHeight());

		batch.end();
	}

}
