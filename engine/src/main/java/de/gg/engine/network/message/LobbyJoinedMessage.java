package de.gg.engine.network.message;

/**
 * This message is sent upon a {@link LobbyJoinRequestMessage} if the server
 * accepts the request.
 */
public final class LobbyJoinedMessage {

	private short clientNetworkId;

	public LobbyJoinedMessage() {
		// default public constructor
	}

	public LobbyJoinedMessage(short clientNetworkId) {
		this.clientNetworkId = clientNetworkId;
	}

	public short getClientNetworkId() {
		return clientNetworkId;
	}

}
