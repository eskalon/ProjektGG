package de.gg.game.input;

import com.badlogic.gdx.Input.Keys;

import de.gg.engine.input.BaseInputMultiplexer;
import de.gg.engine.log.Log;
import de.gg.engine.utils.ScreenshotUtils;
import de.gg.game.core.ProjektGG;

/**
 * This input multiplexer takes care of application wide key binds.
 */
public class ProjektGGInputMultiplexer extends BaseInputMultiplexer {

	protected ProjektGG game;

	public ProjektGGInputMultiplexer(ProjektGG game) {
		this.game = game;
	}

	@Override
	public final boolean keyDown(int keycode) {
		if (keyDownApplicationWide(keycode))
			return true;

		return super.keyDown(keycode); // iterate over the registered input
										// processors
	}

	protected boolean keyDownApplicationWide(int keycode) {
		if (keycode == Keys.F12) { // SCREENSHOTS
			Log.info("Input", "Screenshot taken");
			ScreenshotUtils.takeScreenshot();

			return true;
		} else if (keycode == Keys.F2) { // FPS COUNTER
			game.setFPSCounter(!game.showFPSCounter());

			return true;
		}

		return false;
	}

}