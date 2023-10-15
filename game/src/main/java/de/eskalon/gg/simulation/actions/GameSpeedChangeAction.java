package de.eskalon.gg.simulation.actions;

import de.eskalon.commons.net.packets.data.IPlayerAction;

public final class GameSpeedChangeAction implements IPlayerAction {

	private boolean speedUp;

	public GameSpeedChangeAction() {
		// default public constructor
	}

	public GameSpeedChangeAction(boolean speedUp) {
		this.speedUp = speedUp;
	}

	public boolean isSpeedUp() {
		return speedUp;
	}

}
