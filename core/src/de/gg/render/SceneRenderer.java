package de.gg.render;

import java.util.List;

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

import de.gg.entity.Building;
import de.gg.entity.City;

public class SceneRenderer implements Disposable {

	private PerspectiveCamera camera;

	private City city;

	private ModelBatch modelBatch, outlineModelBatch;
	public Environment environment;

	public SceneRenderer(PerspectiveCamera camera, City city,
			String fragmentShader) {
		this.camera = camera;
		this.city = city;

		Config config = new Config();
		config.fragmentShader = fragmentShader;
		outlineModelBatch = new ModelBatch(new DefaultShaderProvider(config));

		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f,
				0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f,
				-0.2f));

		this.camera.position.set(1f, 1f, 1f);
		this.camera.lookAt(0, 0, 0);
		this.camera.near = 1f;
		this.camera.far = 300f;
		this.camera.update();
	}

	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| GL20.GL_STENCIL_BUFFER_BIT);

		if (city.getSkybox() != null)
			modelBatch.render(city.getSkybox());

		// Drawing to set stencil
		Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
		Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 1);
		Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
		Gdx.gl.glStencilMask(1);
		Gdx.gl.glClearStencil(0);
		Gdx.gl.glClear(GL20.GL_STENCIL_BUFFER_BIT);

		modelBatch.begin(camera);
		renderModels(city.getBuildings());
		modelBatch.end();

		// Now only draw, where previously not drawn
		Gdx.gl.glStencilFunc(GL20.GL_EQUAL, 0, 1);
		Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_KEEP);
		Gdx.gl.glStencilMask(0x00);

		outlineModelBatch.begin(camera);
		renderOutlines(city.getBuildings());
		outlineModelBatch.end();

		Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
	}

	public void renderModels(List<Building> buildings) {
		for (final Building building : buildings) {
			if (!building.getRenderData().isSelected // diese Bedingung nur
														// testweise
					&& building.getRenderData().isVisibleForCamera(camera)) {
				modelBatch.render(building.getRenderData(), environment);
			}
		}
	}

	public void renderOutlines(List<Building> buildings) {
		// TODO scale models
		for (final Building building : buildings) {
			if (building.getRenderData().isSelected
					&& building.getRenderData().isVisibleForCamera(camera)) {
				outlineModelBatch.render(building.getRenderData(), environment);
			}
		}
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
	}
}
