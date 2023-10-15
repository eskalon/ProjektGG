package de.eskalon.commons.net.packets.handshake;

import de.eskalon.commons.net.packets.data.LobbyData;

/**
 * This message is sent upon a {@link RequestJoiningLobbyPacket} if the server
 * accepts the request.
 */
public final class LobbyJoinedPacket {

	private short clientNetworkId;
	private LobbyData lobbyData;

	public LobbyJoinedPacket() {
		// default public constructor
	}

	public LobbyJoinedPacket(short clientNetworkId, LobbyData lobbyData) {
		this.clientNetworkId = clientNetworkId;
		this.lobbyData = lobbyData;
	}

	public short getClientNetworkId() {
		return clientNetworkId;
	}

	public LobbyData getLobbyData() {
		return lobbyData;
	}

}
