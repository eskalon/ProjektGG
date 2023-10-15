package de.eskalon.commons.net.packets.lockstep;

import java.util.List;

import de.eskalon.commons.net.packets.data.PlayerActionsWrapper;

public final class ActionsDistributionPacket {

	private int turn;
	private List<PlayerActionsWrapper> actions;

	public ActionsDistributionPacket() {
		// default public constructor
	}

	public ActionsDistributionPacket(int turn,
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
