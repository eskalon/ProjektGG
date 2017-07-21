package dev.gg.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

import dev.gg.util.ScreenshotUtils;

public class GameInputProcessor implements InputProcessor {

	private InputProcessor inputProcessor;

	public GameInputProcessor() {
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.F12) {
			Gdx.app.debug("Input", "Screenshot taken");
			ScreenshotUtils.takeScreenshot();
		}

		if (inputProcessor != null)
			return inputProcessor.keyDown(keycode);

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (inputProcessor != null)
			return inputProcessor.keyUp(keycode);

		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if (inputProcessor != null)
			return inputProcessor.keyTyped(character);

		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		if (inputProcessor != null)
			return inputProcessor.touchDown(x, y, pointer, button);

		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if (inputProcessor != null)
			return inputProcessor.touchUp(x, y, pointer, button);

		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		if (inputProcessor != null)
			return inputProcessor.touchDragged(x, y, pointer);

		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		if (inputProcessor != null)
			return inputProcessor.mouseMoved(x, y);

		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (inputProcessor != null)
			return inputProcessor.scrolled(amount);

		return false;
	}

	public void setInputProcessor(InputProcessor inputProcessor) {
		this.inputProcessor = inputProcessor;
	}

}