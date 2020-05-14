package de.gg.game.input;

import com.badlogic.gdx.Input.Keys;

import de.eskalon.commons.input.DefaultInputProcessor;
import de.eskalon.commons.input.KeyBinding;
import de.gg.game.core.GameSettings;
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

	public GameSpeedInputProcessor(GameSettings settings) {
		this.increaseSpeedKey = settings.getKeybind("speedUpTime", Keys.PLUS);
		this.decreseSpeedKey = settings.getKeybind("speedDownTime", Keys.MINUS);
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
