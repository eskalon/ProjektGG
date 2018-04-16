package de.gg.camera;

import com.badlogic.gdx.graphics.PerspectiveCamera;

/**
 * A wrapper for the game camera to easily allow transformations, etc.
 */
public class CameraWrapper {

	private PerspectiveCamera camera;

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
	}

}
