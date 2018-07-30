package de.gg.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

import de.gg.camera.CameraWrapper;
import de.gg.setting.GameSettings;

/**
 * @see CameraInputController The libgdx class this is based on.
 */
public class MapMovementInputController
		implements DefaultInputProcessor, SettableKeysProcessor {

	public CameraWrapper camera;

	// Key binds
	public int rotateButton = Buttons.RIGHT;
	public int forwardKey;
	public int backwardKey;
	public int rightKey;
	public int leftKey;

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

	public MapMovementInputController(final CameraWrapper camera,
			final GameSettings settings) {
		this.camera = camera;

		loadKeybinds(settings);
	}

	@Override
	public void loadKeybinds(GameSettings settings) {
		resetInput();

		this.forwardKey = settings.getForwardKey();
		this.backwardKey = settings.getBackwardKey();
		this.rightKey = settings.getRightKey();
		this.leftKey = settings.getLeftKey();
	}

	public void update() {
		if (rightPressed || leftPressed || forwardPressed || backwardPressed) {
			final float delta = Gdx.graphics.getDeltaTime();

			if (rightPressed) {
				camera.translateOnXYPlane(270, delta * translateUnits);
			}
			if (leftPressed) {
				camera.translateOnXYPlane(90, delta * translateUnits);
			}
			if (forwardPressed) {
				camera.translateOnXYPlane(0, delta * translateUnits);
			}
			if (backwardPressed) {
				camera.translateOnXYPlane(180, delta * translateUnits);
			}

			camera.update();
		}
	}

	private void resetInput() {
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
			camera.rotateAroundTarget(deltaX * -rotationSpeed,
					deltaY * rotationSpeed);
		}

		camera.update();
		return true;
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
		if (keycode == forwardKey)
			forwardPressed = true;
		else if (keycode == backwardKey)
			backwardPressed = true;
		else if (keycode == rightKey)
			rightPressed = true;
		else if (keycode == leftKey)
			leftPressed = true;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == forwardKey)
			forwardPressed = false;
		else if (keycode == backwardKey)
			backwardPressed = false;
		else if (keycode == rightKey)
			rightPressed = false;
		else if (keycode == leftKey)
			leftPressed = false;
		return false;
	}

}
