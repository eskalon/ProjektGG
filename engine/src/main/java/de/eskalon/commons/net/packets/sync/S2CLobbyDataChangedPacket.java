package de.eskalon.commons.net.packets.sync;

import de.eskalon.commons.net.packets.data.LobbyData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This message is sent by the server to notify the clients of changes to the
 * lobby data.
 */
@AllArgsConstructor
@NoArgsConstructor
public final class S2CLobbyDataChangedPacket {

	private @Getter ChangeType changeType;
	private @Getter LobbyData lobbyData;

	public enum ChangeType {
		PLAYER_JOINED, PLAYER_LEFT, DATA_CHANGE;
	}

}
