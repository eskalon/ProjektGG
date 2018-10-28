package de.gg.game.input;

import com.badlogic.gdx.Input.Keys;

import de.gg.engine.input.DefaultInputProcessor;
import de.gg.engine.input.SettableKeysProcessor;
import de.gg.engine.setting.ConfigHandler;
import de.gg.game.network.rmi.ClientsideActionHandler;
import de.gg.game.network.rmi.SlaveActionListener;

/**
 * This input processor takes care of relaying the game speed actions to the
 * {@link SlaveActionListener}.
 */
public class GameSpeedInputProcessor
		implements DefaultInputProcessor, SettableKeysProcessor {

	private int INCREASE_SPEED_KEY;
	private int DECREASE_SPEED_KEY;
	private ClientsideActionHandler actionHandler;

	public void setClientActionHandler(ClientsideActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	@Override
	public void loadKeybinds(ConfigHandler settings) {
		this.INCREASE_SPEED_KEY = settings.getInt("speedUpKey", Keys.PLUS);
		this.DECREASE_SPEED_KEY = settings.getInt("speedDownKey", Keys.MINUS);
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
