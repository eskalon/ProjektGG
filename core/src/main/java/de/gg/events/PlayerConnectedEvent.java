package de.gg.events;

import de.gg.network.LobbyPlayer;

/**
 * Posted when a player connects.
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
