package de.eskalon.gg.input;

import de.eskalon.commons.input.DefaultInputListener;
import de.eskalon.gg.screens.game.MapScreen.GameMapAxisBinding;
import de.eskalon.gg.screens.game.MapScreen.GameMapBinaryBinding;
import de.eskalon.gg.simulation.GameHandler;
import de.eskalon.gg.simulation.actions.GameSpeedChangeAction;

/**
 * This input processor catches actions with regard to the game speed.
 */
public class GameSpeedInputProcessor implements
		DefaultInputListener<GameMapAxisBinding, GameMapBinaryBinding> {

	private GameHandler handler;

	public GameSpeedInputProcessor(GameHandler handler) {
		this.handler = handler;
	}

	@Override
	public boolean on(GameMapBinaryBinding id) {
		if (id == GameMapBinaryBinding.INCREASE_SPEED) {
			handler.executeAction(new GameSpeedChangeAction(true));
			return true;
		}
		if (id == GameMapBinaryBinding.DECREASE_SPEED) {
			handler.executeAction(new GameSpeedChangeAction(false));
			return true;
		}
		return false;
	}

}
