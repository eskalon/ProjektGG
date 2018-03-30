package de.gg.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Disposable;

import de.gg.entity.City;

public class SceneRenderer implements Disposable {

	private PerspectiveCamera camera;

	private City city;

	private ModelBatch modelBatch;
	public Environment environment;

	private BuildingRenderer modelRenderer;

	public SceneRenderer(PerspectiveCamera camera, City city) {
		this.camera = camera;
		this.city = city;

		this.modelRenderer = new BuildingRenderer(camera);

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
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(camera);

		modelRenderer.render(modelBatch, environment, city.getBuildings());

		if (city.getSkybox() != null)
			modelBatch.render(city.getSkybox());

		modelBatch.end();
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
	}
}
