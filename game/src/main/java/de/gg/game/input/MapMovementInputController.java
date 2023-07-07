package de.gg.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

import de.eskalon.commons.input.DefaultInputListener;
import de.eskalon.commons.settings.EskalonSettings;
import de.gg.game.ui.rendering.CameraWrapper;
import de.gg.game.ui.screens.GameMapScreen.GameMapAxisBinding;
import de.gg.game.ui.screens.GameMapScreen.GameMapBinaryBinding;

/**
 * @see CameraInputController The libgdx class this is based on.
 */
public class MapMovementInputController implements
		DefaultInputListener<GameMapAxisBinding, GameMapBinaryBinding> {

	public CameraWrapper camera;

	// Key binds
	private int xAxis, yAxis;

	private boolean start;
	private boolean isTouchDown;
	private int startX, startY;

	public float rotationSpeed = 360f;
	public float translateUnits = 10f;
	public float scrollFactor = -0.1f;

	public MapMovementInputController(CameraWrapper camera,
			EskalonSettings settings) {
		this.camera = camera;

		resetInput();
	}

	public void update(float delta) {
		if (xAxis != 0)
			camera.translateOnXZPlane(270, delta * translateUnits * xAxis);

		if (yAxis != 0)
			camera.translateOnXZPlane(0, delta * translateUnits * yAxis);

		if (xAxis != 0 || yAxis != 0)
			camera.update();
	}

	public void resetInput() {
		xAxis = 0;
		yAxis = 0;
		start = false;
		isTouchDown = false;
	}

	@Override
	public boolean on(GameMapBinaryBinding id) {
		if (id == GameMapBinaryBinding.ROTATE_CAMERA_BUTTON) {
			isTouchDown = true;
			start = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean off(GameMapBinaryBinding id) {
		if (id == GameMapBinaryBinding.ROTATE_CAMERA_BUTTON) {
			isTouchDown = false;
			start = false;
			return true;
		}
		return false;
	}

	@Override
	public boolean moved(int screenX, int screenY) {
		if (start) {
			startX = screenX;
			startY = screenY;
			start = false;
			return true;
		} else if (isTouchDown) {
			final float deltaX = (screenX - startX)
					/ (float) Gdx.graphics.getWidth();
			final float deltaY = (startY - screenY)
					/ (float) Gdx.graphics.getHeight();

			// TODO: fix this!
			camera.rotateAroundTargetOnXZPlane(deltaX * -rotationSpeed,
					deltaY * rotationSpeed);
			camera.update();

			startX = screenX;
			startY = screenY;

			return true;
		}
		return false;
	}

	@Override
	public boolean axisChanged(GameMapAxisBinding id, float value) {
		if (id == GameMapAxisBinding.MOVE_LEFT_RIGHT) {
			xAxis = (int) value;
			return true;
		}
		if (id == GameMapAxisBinding.MOVE_FORWARDS_BACKWARDS) {
			yAxis = (int) value;
			return true;
		}
		if (id == GameMapAxisBinding.ZOOM) {
			camera.zoom(value * scrollFactor * translateUnits);
			camera.update();
			return true;
		}
		return false;
	}

}
