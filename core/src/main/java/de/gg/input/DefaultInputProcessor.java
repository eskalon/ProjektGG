package de.gg.input;

import com.badlogic.gdx.InputProcessor;

public interface DefaultInputProcessor extends InputProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public default boolean keyDown(int keycode) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public default boolean keyUp(int keycode) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public default boolean keyTyped(char character) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public default boolean touchDown(int screenX, int screenY, int pointer,
			int button) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public default boolean touchUp(int screenX, int screenY, int pointer,
			int button) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public default boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public default boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public default boolean scrolled(int amount) {
		return false;
	}

}
