package de.gg.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class RenderData extends ModelInstance {

	public final Vector3 center = new Vector3();
	public final Vector3 dimensions = new Vector3();
	public final float radius;

	private final static BoundingBox bounds = new BoundingBox();
	private final static Vector3 position = new Vector3();

	public RenderData(Model model, String rootNode, boolean mergeTransform) {
		super(model, rootNode, mergeTransform);
		calculateBoundingBox(bounds);
		bounds.getCenter(center);
		bounds.getDimensions(dimensions);
		radius = dimensions.len() / 2f;
	}

	/**
	 * @return -1 on no intersection, or when there is an intersection: the
	 *         squared distance between the center of this object and the point
	 *         on the ray closest to this object when there is intersection.
	 */
	public float intersects(Ray ray) {
		transform.getTranslation(position).add(center);
		final float len = ray.direction.dot(position.x - ray.origin.x,
				position.y - ray.origin.y, position.z - ray.origin.z);
		if (len < 0f)
			return -1f;
		float dist2 = position.dst2(ray.origin.x + ray.direction.x * len,
				ray.origin.y + ray.direction.y * len,
				ray.origin.z + ray.direction.z * len);
		return (dist2 <= radius * radius) ? dist2 : -1f;
	}

	/**
	 * Takes care of the frustum culling calculations.
	 * 
	 * @param cam
	 *            The camera.
	 * @return Whether the object is visible for the camera.
	 */
	public boolean isVisibleForCamera(final Camera cam) {
		return cam.frustum.sphereInFrustum(
				transform.getTranslation(position).add(center), radius);
	}

}
