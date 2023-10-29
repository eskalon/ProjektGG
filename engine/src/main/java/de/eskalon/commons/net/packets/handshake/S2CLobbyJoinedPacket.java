package de.eskalon.commons.net.packets.handshake;

import de.eskalon.commons.net.packets.data.LobbyData;

/**
 * This message is sent upon a {@link C2SRequestJoiningLobbyPacket} if the server
 * accepts the request.
 */
public final class S2CLobbyJoinedPacket {

	private short clientNetworkId;
	private LobbyData lobbyData;

	public S2CLobbyJoinedPacket() {
		// default public constructor
	}

	public S2CLobbyJoinedPacket(short clientNetworkId, LobbyData lobbyData) {
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
