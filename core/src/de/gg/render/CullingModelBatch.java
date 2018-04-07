package de.gg.render;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * This model batch takes care of the frustum culling calculations.
 * 
 * @param cam
 *            The camera.
 * @return Whether the object is visible for the camera.
 */
public class CullingModelBatch extends ModelBatch {

	private ObjectMap<Mesh, Float> radiuses = new ObjectMap<Mesh, Float>();

	private Vector3 tmp = new Vector3();

	@Override
	public void flush() {
		Iterator<Renderable> iter = renderables.iterator();

		while (iter.hasNext()) {
			Renderable renderable = iter.next();
			renderable.worldTransform.getTranslation(tmp);
			if (!camera.frustum.sphereInFrustumWithoutNearFar(tmp,
					getRadiusOfMesh(renderable.meshPart.mesh))) {
				iter.remove();
			}
		}

		System.out.println(renderables.size);
		super.flush();
	}

	private float getRadiusOfMesh(Mesh mesh) {
		Float radius = radiuses.get(mesh);
		if (radius != null) {
			return radius;
		}

		Vector3 dimensions = new Vector3();

		mesh.calculateBoundingBox().getDimensions(dimensions);
		radius = Math.max(Math.max(dimensions.x, dimensions.y), dimensions.z);

		radiuses.put(mesh, radius);
		return radius;
	}

}
