package de.gg.game.input;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

import de.eskalon.commons.input.DefaultInputProcessor;

/**
 * This input processor takes care of a back action (right click or escape).
 */
public abstract class BackInputProcessor implements DefaultInputProcessor {

	private static final int BACK_KEY = Keys.ESCAPE;
	private static final int BACK_BUTTON = Buttons.RIGHT;

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == BACK_KEY) {
			onBackAction();
			return true;
		}

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer,
			int button) {
		if (button == BACK_BUTTON) {
			onBackAction();
			return true;
		}

		return false;
	}

	public abstract void onBackAction();

}
