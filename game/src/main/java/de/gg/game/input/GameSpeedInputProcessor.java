package de.gg.game.input;

import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.settings.EskalonSettings;
import de.eskalon.commons.settings.KeyBinding;
import de.gg.game.network.rmi.ClientsideActionHandler;
import de.gg.game.network.rmi.SlaveActionListener;

/**
 * This input processor takes care of relaying the game speed actions to the
 * {@link SlaveActionListener}.
 */
public class GameSpeedInputProcessor implements DefaultInputProcessor {

	private KeyBinding increaseSpeedKey;
	private KeyBinding decreseSpeedKey;
	private ClientsideActionHandler actionHandler;

	public GameSpeedInputProcessor(EskalonSettings settings) {
		this.increaseSpeedKey = settings.getKeybind("speedUpTime");
		this.decreseSpeedKey = settings.getKeybind("speedDownTime");
	}

	public void setClientActionHandler(ClientsideActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (increaseSpeedKey.isTriggered(keycode)) {
			actionHandler.increaseGameSpeed();

			return true;
		}
		if (decreseSpeedKey.isTriggered(keycode)) {
			actionHandler.decreaseGameSpeed();

			return true;
		}
		return false;
	}

}
