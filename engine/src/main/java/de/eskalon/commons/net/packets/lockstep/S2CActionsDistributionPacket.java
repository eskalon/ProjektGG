package de.eskalon.commons.net.packets.lockstep;

import java.util.List;

import de.eskalon.commons.net.packets.data.PlayerActionsWrapper;

public final class S2CActionsDistributionPacket {

	private int turn;
	private List<PlayerActionsWrapper> actions;

	public S2CActionsDistributionPacket() {
		// default public constructor
	}

	public S2CActionsDistributionPacket(int turn,
			List<PlayerActionsWrapper> actions) {
		this.turn = turn;
		this.actions = actions;
	}

	public int getTurn() {
		return turn;
	}

	public List<PlayerActionsWrapper> getActions() {
		return actions;
	}

}
