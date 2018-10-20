package de.gg.input;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

import de.gg.core.ProjektGG;
import de.gg.utils.ScreenshotUtils;
import de.gg.utils.log.Log;

/**
 * This input multiplexer allows the game to use application wide key binds
 * (e.g. a key for taking a screenshot).
 */
public class GameInputMultiplexer extends InputMultiplexer {

	private ProjektGG game;

	public GameInputMultiplexer(ProjektGG game) {
		this.game = game;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.F12) { // SCREENSHOTS
			Log.info("Input", "Screenshot taken");
			ScreenshotUtils.takeScreenshot();

			return true;
		} else if (keycode == Keys.F2) { // FPS COUNTER
			game.setFPSCounter(!game.showFPSCounter());

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

	/**
	 * Removes all input processors contained in the given array.
	 *
	 * @param processors
	 *            The processor to remove.
	 * @see #removeProcessor(InputProcessor)
	 */
	public void removeInputProcessors(Array<InputProcessor> processors) {
		for (InputProcessor p : processors) {
			removeProcessor(p);
		}
	}

}