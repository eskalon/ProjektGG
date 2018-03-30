package dev.gg.render;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class RenderData extends ModelInstance {
	
	public final Vector3 center = new Vector3();
	public final Vector3 dimensions = new Vector3();
	public final float radius;

	private final static BoundingBox bounds = new BoundingBox();

	public RenderData(Model model, String rootNode, boolean mergeTransform) {
            super(model, rootNode, mergeTransform);
            calculateBoundingBox(bounds);
            bounds.getCenter(center);
            bounds.getDimensions(dimensions);
            radius = dimensions.len() / 2f;
        }

}
