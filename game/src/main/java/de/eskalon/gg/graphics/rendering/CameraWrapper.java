package de.eskalon.gg.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * A wrapper for the game camera to easily allow transformations, etc.
 */
public class CameraWrapper {

	/**
	 * The actual camera.
	 */
	private PerspectiveCamera camera;
	/**
	 * Offsets the look at vector of the camera from which the rotation point is
	 * calculated. The offset can manipulate the look at to be at the bottom of
	 * the screen (-1.0f), the top of the screen (1.0f) or something in between.
	 */
	private final float ROTATION_OFFSET_Y = -0.5f;
	private final Vector3 tmp = new Vector3();
	private final Vector3 tmp2 = new Vector3();
	/**
	 * The target to rotate around.
	 */
	private Vector3 target = new Vector3();
	/**
	 * Whether to update the target on translation.
	 */
	private boolean updateTargetOnTranslation = true;
	/**
	 * Whether to update the target on zooming.
	 */
	private boolean updateTargetOnZoom = false;

	public CameraWrapper(PerspectiveCamera camera) {
		this.camera = camera;
	}

	/**
	 * @return the wrapped camera.
	 */
	public PerspectiveCamera getCamera() {
		return camera;
	}

	/**
	 * Recalculates the projection and view matrix of this camera and the
	 * Frustum planes. Use this after you've manipulated any of the attributes
	 * of the camera.
	 *
	 * @see PerspectiveCamera#update()
	 *
	 */
	public void update() {
		camera.update();
	}

	/**
	 * Sets the camera to the given position.
	 *
	 * @param x
	 *            the x position
	 * @param y
	 *            the y position
	 * @param z
	 *            the z position
	 */
	public void setPosition(float x, float y, float z) {
		if (updateTargetOnTranslation)
			target.add(camera.position.x - x, camera.position.y - y,
					camera.position.z - z);

		this.camera.position.set(x, y, z);
	}

	/**
	 * Sets the target around which the camera is rotated.
	 *
	 * @param x
	 *            the x position
	 * @param y
	 *            the y position
	 * @param z
	 *            the z position
	 */
	public void setTarget(float x, float y, float z) {
		this.camera.position.set(x, y, z);

		if (updateTargetOnTranslation)
			target.add(tmp);
	}

	/**
	 * Moves the camera by the given amount on each axis.
	 *
	 * @param x
	 *            the displacement on the x-axis
	 * @param y
	 *            the displacement on the y-axis
	 * @param z
	 *            the displacement on the z-axis
	 */
	public void translate(float x, float y, float z) {
		this.camera.translate(x, y, z);

		if (updateTargetOnTranslation)
			target.add(x, y, z);
	}

	/**
	 * Moves the camera by the given vector.
	 *
	 * @param vec
	 *            the displacement vector
	 */
	public void translate(Vector3 vec) {
		this.camera.translate(vec);

		if (updateTargetOnTranslation)
			target.add(vec);
	}

	/**
	 * Rotates the direction and up vector of this camera by the given angle
	 * around the given axis, with the axis attached to given point. The
	 * direction and up vector will not be orthogonalized.
	 *
	 * @param point
	 *            the point to attach the axis to
	 * @param axis
	 *            the axis to rotate around
	 * @param angle
	 *            the angle, in degrees
	 */
	public void rotateAround(Vector3 point, Vector3 axis, float angle) {
		camera.rotateAround(point, axis, angle);
	}

	/**
	 * Rotates the camera around the target.
	 *
	 * @param angleX
	 *            the x angle in degrees
	 * @param angleY
	 *            the y angle in degrees
	 * @see #rotateAround(Vector3, Vector3, float)
	 */
	public void rotateAroundTargetOnXZPlane(float angleX, float angleY) {
		// Calculates the axis, that is perpendicular to UP and DIRECTION of the
		// camera
		tmp2.set(camera.direction).crs(camera.up);
		tmp2.y = 0;

		// Intersection of the camera direction with the ground floor.
		// Also adds some offset in screen-space Y direction to make the
		// rotation feel more intuitive.
		Ray ray = camera.getPickRay(Gdx.graphics.getWidth() / 2.0f,
				Gdx.graphics.getHeight() / (2.0f + ROTATION_OFFSET_Y));
		// calculating like this: origin.y + t * direction.y = 0 => intersection
		// with ground floor
		float t = -ray.origin.y / ray.direction.y;
		// target now contains the intersection of the ray and the ground plane
		target.set(ray.origin.add(ray.direction.scl(t)));

		rotateAround(target, Vector3.Y, angleX);
	}

	/**
	 * Translates the camera in the given direction (specified by the angel) on
	 * the x-z-plane. Is commonly used to process key movement events.
	 *
	 * @param angle
	 * @param units
	 */
	public void translateOnXZPlane(int angle, float units) {
		tmp.set(camera.direction).y = 0;
		translate(tmp.rotate(Vector3.Y, angle).scl(units));
	}

	/**
	 * Moves closer in/out to the point the camera is pointing on. Moves "on the
	 * {@linkplain CameraWrapper#getDirection() direction vector}".
	 *
	 * @param amount
	 *            The amount to zoom.
	 */
	public void zoom(float amount) {
		camera.translate(tmp2.set(camera.direction).scl(amount));
		if (updateTargetOnZoom)
			target.add(tmp2);
	}

	/**
	 * @return the unit length direction vector of the camera; the direction the
	 *         camera is facing in
	 */
	public Vector3 getDirection() {
		return camera.direction;
	}

	/**
	 * @return the unit length up vector of the camera; is perpendicular to the
	 *         direction vector
	 */
	public Vector3 getUp() {
		return camera.up;
	}

}
