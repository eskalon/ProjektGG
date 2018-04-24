package de.gg.input;

import com.badlogic.gdx.Input.Keys;

import de.gg.network.NetworkHandler;

/**
 * This input processor takes care of relaying the game speed actions to the
 * {@link NetworkHandler}.
 */
public class GameSpeedInputProcessor implements DefaultInputProcessor {

	private int INCREASE_SPEED_KEY = Keys.PLUS;
	private int DECREASE_SPEED_KEY = Keys.MINUS;
	private NetworkHandler handler;

	public GameSpeedInputProcessor(NetworkHandler handler) {
		this.handler = handler;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (INCREASE_SPEED_KEY == keycode) {
			handler.increaseGameSpeed();

			return true;
		}
		if (DECREASE_SPEED_KEY == keycode) {
			handler.decreaseGameSpeed();

			return true;
		}
		return false;
	}

}
