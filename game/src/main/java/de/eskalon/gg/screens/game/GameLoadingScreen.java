package de.eskalon.gg.screens.game;

import java.util.concurrent.TimeUnit;

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

import de.damios.guacamole.Stopwatch;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.misc.TaskExecutor;
import de.eskalon.commons.screens.AbstractEskalonUIScreen;
import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.gg.core.ProjektGGApplicationContext;
import de.eskalon.gg.graphics.rendering.GameRenderer;
import de.eskalon.gg.graphics.rendering.SelectableRenderData;
import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.entities.BuildingSlot;
import de.eskalon.gg.simulation.model.types.BuildingType;

/**
 * This screen takes care of loading the 3D models, initializing the game world
 * and setting up the 3D scene.
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

	private TaskExecutor taskExecutor;
	private boolean isDone = false;
	private int loadingTicksPerSecond = 30;

	private Stopwatch stopwatch;

	@Override
	public void show() {
		super.show();

		setImage(backgroundTexture);

		taskExecutor = new TaskExecutor();
		stopwatch = Stopwatch.createStarted();

		World world = appContext.getGameHandler().getSimulation().getWorld();

		/* Load game assets */
		// This has to be done here, because the game data isn't loaded yet at
		// the start of AssetLoadingScreen
		assetManager.load(world.getMap().getSkyboxPath(), Model.class);

		for (BuildingType t : BuildingType.values()) {
			assetManager.load(t.getModelPath(), Model.class);
		}

		/* Init 3D scene */
		// Create ModelInstances for the buildings
		for (BuildingSlot s : world.getBuildingSlots()) {
			if (s.isBuiltOn()) {
				taskExecutor.execute(() -> {
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
		taskExecutor.execute(() -> {
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
		taskExecutor.execute(() -> {
			appContext.setGameRenderer(
					EskalonInjector.instance().getInstance(GameRenderer.class));
			appContext.getGameRenderer().init();
		});
	}

	@Override
	public void render(float delta) {
		// Execute stuff
		if (!isDone && assetManager.update(1000 / loadingTicksPerSecond)
				&& taskExecutor.update(1000 / loadingTicksPerSecond)) {
			isDone = true;
			LOG.info("[CLIENT] Game loading finished in %d miliseconds",
					stopwatch.getTime(TimeUnit.MILLISECONDS));
			screenManager.pushScreen(RoundEndScreen.class, "simple_zoom");
		}

		float progress = (assetManager.getProgress()
				+ taskExecutor.getProgress()) / 2; // TODO: introduce a better
													// approximation of progress

		// Render background
		super.render(delta);

		// Render Progress bar
		viewport.apply();
		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.begin();

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

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height, true);
	}

}
