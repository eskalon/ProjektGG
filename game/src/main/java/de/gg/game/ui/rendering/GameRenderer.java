package de.gg.game.ui.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader.Config;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.utils.Disposable;

import de.gg.game.model.World;
import de.gg.game.model.entities.Building;
import de.gg.game.model.entities.BuildingSlot;

/**
 * This class is responsible for rendering the city.
 */
public class GameRenderer implements Disposable {

	private PerspectiveCamera camera;

	private ModelBatch modelBatch, outlineModelBatch;
	public Environment environment;

	public GameRenderer(PerspectiveCamera camera, String fragmentShader) {
		this.camera = camera;

		Config config = new Config();
		config.fragmentShader = fragmentShader;
		outlineModelBatch = new CullingModelBatch(
				new DefaultShaderProvider(config));

		modelBatch = new CullingModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4F,
				0.4F, 0.4F, 1F));
		environment.add(new DirectionalLight().set(0.8F, 0.8F, 0.8F, -1F, -0.8F,
				-0.2F));
	}

	public void resetCamera() {
		this.camera.position.set(1F, 1F, 1F);
		this.camera.lookAt(0, 0, 0);
		this.camera.near = 1f;
		this.camera.far = 300f;
		this.camera.update();
	}

	public void render(World world) {
		// Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
		// Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| GL20.GL_STENCIL_BUFFER_BIT);

		if (world.getSkybox() != null)
			modelBatch.render(world.getSkybox());

		// Drawing to set stencil
		Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
		Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 1);
		Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
		Gdx.gl.glStencilMask(1);
		Gdx.gl.glClearStencil(0);
		Gdx.gl.glClear(GL20.GL_STENCIL_BUFFER_BIT);

		modelBatch.begin(camera);
		renderModels(world.getBuildingSlots());
		modelBatch.end();

		// Now only draw, where previously not drawn
		Gdx.gl.glStencilFunc(GL20.GL_EQUAL, 0, 1);
		Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_KEEP);
		Gdx.gl.glStencilMask(0x00);

		outlineModelBatch.begin(camera);
		renderOutlines(world.getBuildingSlots());
		outlineModelBatch.end();

		Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
	}

	public void renderModels(BuildingSlot[] buildings) {
		for (final BuildingSlot slot : buildings) {
			if (slot.isBuiltOn()) {
				Building building = slot.getBuilding();

				if (!building.getRenderData().isSelected) {// diese Bedingung
															// nur
															// testweise
					modelBatch.render(building.getRenderData(), environment);
				}
			}
		}
	}

	public void renderOutlines(BuildingSlot[] buildings) {
		// TODO scale models
		for (final BuildingSlot slot : buildings) {
			if (slot.isBuiltOn()) {
				Building building = slot.getBuilding();
				if (building.getRenderData().isSelected) {
					outlineModelBatch.render(building.getRenderData(),
							environment);
				}
			}
		}
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
	}
}
