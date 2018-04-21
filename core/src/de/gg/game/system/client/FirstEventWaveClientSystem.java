package de.gg.game.system.client;

import de.gg.game.entity.Player;
import de.gg.game.system.ProcessingSystem;

/**
 * This system processes after 60 seconds and takes care of the first wave of
 * events on the client side.
 */
public class FirstEventWaveClientSystem extends ProcessingSystem<Player> {

	public FirstEventWaveClientSystem() {
	}

	@Override
	public void process(short id, Player p) {
		// TODO inform about open positions

		// TODO inform about possibility to buy citizen status
	}

	@Override
	public boolean isProcessedContinuously() {
		return false;
	}

	@Override
	public int getTickRate() {
		return 600;
	}

}
