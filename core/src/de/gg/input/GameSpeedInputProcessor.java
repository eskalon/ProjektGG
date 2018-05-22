package de.gg.input;

import de.gg.network.rmi.ClientActionHandler;
import de.gg.network.rmi.SlaveActionListener;
import de.gg.setting.GameSettings;

/**
 * This input processor takes care of relaying the game speed actions to the
 * {@link SlaveActionListener}.
 */
public class GameSpeedInputProcessor
		implements
			DefaultInputProcessor,
			SettableKeysProcessor {

	private int INCREASE_SPEED_KEY;
	private int DECREASE_SPEED_KEY;
	private ClientActionHandler actionHandler;

	public GameSpeedInputProcessor(GameSettings settings,
			ClientActionHandler actionHandler) {
		this.actionHandler = actionHandler;
		loadKeybinds(settings);
	}

	@Override
	public void loadKeybinds(GameSettings settings) {
		this.INCREASE_SPEED_KEY = settings.getSpeedUpKey();
		this.DECREASE_SPEED_KEY = settings.getSpeedDownKey();
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
