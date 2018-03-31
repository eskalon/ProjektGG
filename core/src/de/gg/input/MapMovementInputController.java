package de.gg.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;

/**
 * @see CameraInputController The libgdx class this is based on.
 */
public class MapMovementInputController implements DefaultInputProcessor {

	public Camera camera;

	// Key binds
	public int rotateButton = Buttons.RIGHT;
	public int forwardKey = Keys.W;
	public int backwardKey = Keys.S;
	public int rightKey = Keys.A;
	public int leftKey = Keys.D;

	protected boolean forwardPressed;
	protected boolean backwardPressed;
	protected boolean rightPressed;
	protected boolean leftPressed;
	/** The current (first) button being pressed. */
	protected int button = -1;

	private float startX, startY;
	private final Vector3 Y_AXIS = new Vector3(0, 1, 0);

	private final Vector3 tmp = new Vector3();
	private final Vector3 tmp2 = new Vector3();

	public float rotateAngle = 360f;
	public float translateUnits = 10f;
	public float scrollFactor = -0.1f;

	/** The target to rotate around. */
	public Vector3 target = new Vector3();
	/** Whether to update the target on forward */
	public boolean forwardTarget = true;
	/** Whether to update the target on scroll */
	public boolean scrollTarget = false;

	public MapMovementInputController(final Camera camera) {
		this.camera = camera;
	}

	public void update() {
		if (rightPressed || leftPressed || forwardPressed || backwardPressed) {
			final float delta = Gdx.graphics.getDeltaTime();
			tmp.set(camera.direction).y = 0;

			if (rightPressed) {
				camera.translate(
						tmp.rotate(Y_AXIS, 90).scl(delta * translateUnits));
				if (forwardTarget)
					target.add(tmp);
			}
			if (leftPressed) {
				camera.translate(
						tmp.rotate(Y_AXIS, 270).scl(delta * translateUnits));
				if (forwardTarget)
					target.add(tmp);
			}
			if (forwardPressed) {
				camera.translate(
						tmp.rotate(Y_AXIS, 0).scl(delta * translateUnits));
				if (forwardTarget)
					target.add(tmp);
			}
			if (backwardPressed) {
				camera.translate(
						tmp.rotate(Y_AXIS, 180).scl(delta * translateUnits));
				if (forwardTarget)
					target.add(tmp);
			}

			camera.update();
		}
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
			tmp2.set(camera.direction).crs(camera.up).y = 0f;
			camera.rotateAround(target, tmp2.nor(), deltaY * rotateAngle);
			camera.rotateAround(target, Vector3.Y, deltaX * -rotateAngle);
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
		return zoom(amount * scrollFactor * translateUnits);
	}

	public boolean zoom(float amount) {
		camera.translate(tmp2.set(camera.direction).scl(amount));
		if (scrollTarget)
			target.add(tmp2);

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
