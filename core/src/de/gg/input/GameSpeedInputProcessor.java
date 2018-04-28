package de.gg.input;

import com.badlogic.gdx.Input.Keys;

import de.gg.network.rmi.ClientActionHandler;
import de.gg.network.rmi.SlaveActionListener;

/**
 * This input processor takes care of relaying the game speed actions to the
 * {@link SlaveActionListener}.
 */
public class GameSpeedInputProcessor implements DefaultInputProcessor {

	private int INCREASE_SPEED_KEY = Keys.PLUS;
	private int DECREASE_SPEED_KEY = Keys.MINUS;
	private ClientActionHandler actionHandler;

	public GameSpeedInputProcessor(ClientActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (INCREASE_SPEED_KEY == keycode) {
			actionHandler.increaseGameSpeed();

			return true;
		}
		if (DECREASE_SPEED_KEY == keycode) {
			actionHandler.decreaseGameSpeed();

			return true;
		}
		return false;
	}

}
