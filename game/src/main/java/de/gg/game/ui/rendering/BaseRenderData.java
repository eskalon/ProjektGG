package de.gg.game.ui.rendering;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

/**
 * This class represents a concrete instance of a model.
 */
public class BaseRenderData extends ModelInstance {

	public final Vector3 center = new Vector3();
	public final Vector3 dimensions = new Vector3();
	public final float radius;

	private static final BoundingBox bounds = new BoundingBox();
	private static final Vector3 position = new Vector3();

	public BaseRenderData(Model model) {
		super(model);
		this.calculateBoundingBox(bounds);
		bounds.getCenter(center);
		bounds.getDimensions(dimensions);
		radius = dimensions.len() / 2f;
	}

	public BaseRenderData(Model scene, String rootNode,
			boolean mergeTransform) {
		super(scene, rootNode, mergeTransform);
		this.calculateBoundingBox(bounds);
		bounds.getCenter(center);
		bounds.getDimensions(dimensions);
		radius = dimensions.len() / 2f;
	}

	/**
	 * @return {@code -1} on no intersection; when there is an
	 *         intersection: the squared distance between the center of this
	 *         object and the point on the ray closest to this object.
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

}
