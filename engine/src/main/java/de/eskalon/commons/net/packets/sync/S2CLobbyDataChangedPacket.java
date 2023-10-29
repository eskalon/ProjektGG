package de.eskalon.commons.net.packets.sync;

import de.eskalon.commons.net.packets.data.LobbyData;

/**
 * This message is sent by the server to notify the clients of changes to the
 * lobby data.
 */
public final class S2CLobbyDataChangedPacket {

	private ChangeType changeType;
	private LobbyData lobbyData;

	public S2CLobbyDataChangedPacket() {
		// default public constructor
	}

	public S2CLobbyDataChangedPacket(LobbyData lobbyData, ChangeType changeType) {
		this.lobbyData = lobbyData;
		this.changeType = changeType;
	}

	public LobbyData getLobbyData() {
		return lobbyData;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public enum ChangeType {
		PLAYER_JOINED, PLAYER_LEFT, DATA_CHANGE;
	}

}
