package de.gg.input;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;

import de.gg.util.Log;
import de.gg.util.ScreenshotUtils;

/**
 * This input multiplexer allows the game to use application wide key binds
 * (e.g. a key for taking a screenshot).
 */
public class GameInputMultiplexer extends InputMultiplexer {

	public GameInputMultiplexer() {
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.F12) {
			Log.info("Input", "Screenshot taken");
			ScreenshotUtils.takeScreenshot();

			return true;
		}

		return super.keyDown(keycode);
	}

	/**
	 * Removes all input processors.
	 * 
	 * @see #clear()
	 */
	public void removeInputProcessors() {
		this.clear();
	}

}