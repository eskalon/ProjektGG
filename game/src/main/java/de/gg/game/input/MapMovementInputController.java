package de.gg.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.settings.EskalonSettings;
import de.eskalon.commons.settings.KeyBinding;
import de.gg.engine.ui.rendering.CameraWrapper;

/**
 * @see CameraInputController The libgdx class this is based on.
 */
public class MapMovementInputController implements DefaultInputProcessor {

	public CameraWrapper camera;

	// Key binds
	public int rotateButton = Buttons.RIGHT;
	public KeyBinding forwardKey;
	public KeyBinding backwardKey;
	public KeyBinding rightKey;
	public KeyBinding leftKey;

	protected boolean forwardPressed;
	protected boolean backwardPressed;
	protected boolean rightPressed;
	protected boolean leftPressed;
	/** The current (first) button being pressed. */
	protected int button = -1;

	private float startX, startY;

	public float rotationSpeed = 360f;
	public float translateUnits = 10f;
	public float scrollFactor = -0.1f;

	public MapMovementInputController(CameraWrapper camera,
			EskalonSettings settings) {
		this.camera = camera;

		resetInput();
		this.forwardKey = settings.getKeybind("cameraForward");
		this.backwardKey = settings.getKeybind("cameraBackward");
		this.rightKey = settings.getKeybind("cameraRight");
		this.leftKey = settings.getKeybind("cameraLeft");
	}

	public void update(float delta) {
		if (rightPressed || leftPressed || forwardPressed || backwardPressed) {
			if (rightPressed) {
				camera.translateOnXZPlane(270, delta * translateUnits);
			}
			if (leftPressed) {
				camera.translateOnXZPlane(90, delta * translateUnits);
			}
			if (forwardPressed) {
				camera.translateOnXZPlane(0, delta * translateUnits);
			}
			if (backwardPressed) {
				camera.translateOnXZPlane(180, delta * translateUnits);
			}

			camera.update();
		}
	}

	public void resetInput() {
		rightPressed = false;
		leftPressed = false;
		forwardPressed = false;
		backwardPressed = false;

		button = -1;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer,
			int button) {
		startX = screenX;
		startY = screenY;
		this.button = button;

		return rotateButton == button;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == this.button)
			this.button = -1;
		return rotateButton == button;
	}

	protected boolean process(float deltaX, float deltaY, int button) {
		if (button == rotateButton) {
			camera.rotateAroundTargetOnXZPlane(deltaX * -rotationSpeed,
					deltaY * rotationSpeed);
			camera.update();
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		final float deltaX = (screenX - startX) / Gdx.graphics.getWidth();
		final float deltaY = (startY - screenY) / Gdx.graphics.getHeight();
		startX = screenX;
		startY = screenY;
		return process(deltaX, deltaY, button);
	}

	@Override
	public boolean scrolled(int amount) {
		camera.zoom(amount * scrollFactor * translateUnits);
		camera.update();

		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (forwardKey.isTriggered(keycode))
			forwardPressed = true;
		else if (backwardKey.isTriggered(keycode))
			backwardPressed = true;
		else if (rightKey.isTriggered(keycode))
			rightPressed = true;
		else if (leftKey.isTriggered(keycode))
			leftPressed = true;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (forwardKey.isTriggered(keycode))
			forwardPressed = false;
		else if (backwardKey.isTriggered(keycode))
			backwardPressed = false;
		else if (rightKey.isTriggered(keycode))
			rightPressed = false;
		else if (leftKey.isTriggered(keycode))
			leftPressed = false;
		return false;
	}

}
