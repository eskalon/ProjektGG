package de.gg.render;

import java.util.List;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;

import de.gg.entity.Building;

public class BuildingRenderer {

	private PerspectiveCamera camera;

	private int visibleObjectCount;
	private Vector3 tmpPosition = new Vector3();

	public BuildingRenderer(PerspectiveCamera camera) {
		this.camera = camera;
	}

	public int render(ModelBatch modelBatch, Environment environment,
			List<Building> buildings) {
		visibleObjectCount = 0;

		for (final Building building : buildings) {
			if (building.getRenderData().isVisibleForCamera(camera)) {
				modelBatch.render(building.getRenderData(), environment);
				visibleObjectCount++;
			}
		}

		return visibleObjectCount;
	}

}
