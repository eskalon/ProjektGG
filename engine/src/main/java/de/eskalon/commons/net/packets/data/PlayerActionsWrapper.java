package de.eskalon.commons.net.packets.data;

import java.util.List;

public class PlayerActionsWrapper {

	private short playerId;
	private List<IPlayerAction> actions;

	public PlayerActionsWrapper(short playerId, List<IPlayerAction> actions) {
		this.playerId = playerId;
		this.actions = actions;
	}

	public short getPlayerId() {
		return playerId;
	}

	public List<IPlayerAction> getActions() {
		return actions;
	}

}