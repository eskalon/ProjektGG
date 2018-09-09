package de.gg.events;

import de.gg.network.LobbyPlayer;

/**
 * Posted when one of the clients changes (new icon, getting ready, etc.).
 * Basically the same as a {@link PlayerConnectedEvent}.
 */
public class PlayerChangedEvent {

	private short networkId;
	private LobbyPlayer player;

	public PlayerChangedEvent(short networkId, LobbyPlayer player) {
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
