package de.eskalon.commons.net.packets.handshake;

/**
 * This message is sent if the server rejects a client, e.g. when the server is
 * full or the game versions do not match.
 */
public final class ConnectionRejectedPacket {

	private String message;

	public ConnectionRejectedPacket() {
		// default public constructor
	}

	public ConnectionRejectedPacket(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
