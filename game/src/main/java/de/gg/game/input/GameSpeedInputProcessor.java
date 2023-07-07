package de.gg.game.input;

import de.eskalon.commons.input.DefaultInputListener;
import de.gg.game.network.rmi.ClientsideActionHandler;
import de.gg.game.network.rmi.SlaveActionListener;
import de.gg.game.ui.screens.GameMapScreen.GameMapAxisBinding;
import de.gg.game.ui.screens.GameMapScreen.GameMapBinaryBinding;

/**
 * This input processor takes care of relaying the game speed actions to the
 * {@link SlaveActionListener}.
 */
public class GameSpeedInputProcessor implements
		DefaultInputListener<GameMapAxisBinding, GameMapBinaryBinding> {

	private ClientsideActionHandler actionHandler;

	public void setClientActionHandler(ClientsideActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	@Override
	public boolean on(GameMapBinaryBinding id) {
		if (id == GameMapBinaryBinding.INCREASE_SPEED) {
			actionHandler.increaseGameSpeed();
			return true;
		}
		if (id == GameMapBinaryBinding.DECREASE_SPEED) {
			actionHandler.decreaseGameSpeed();
			return true;
		}
		return false;
	}

}
