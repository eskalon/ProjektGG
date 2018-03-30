package de.gg.event;

import dev.gg.core.LobbyPlayer;

/**
 * Called when a player connects.
 */
public class PlayerConnectedEvent {

	private short networkId;
	private LobbyPlayer player;

	public PlayerConnectedEvent(short networkId, LobbyPlayer player) {
		this.player = player;
		this.networkId = networkId;
	}

	public LobbyPlayer getPlayer() {
		return player;
	}

	public short getNetworkId() {
		return networkId;
	}

}
