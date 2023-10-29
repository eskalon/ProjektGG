package de.eskalon.commons.net.packets.handshake;

/**
 * This message is sent if the server rejects a client, e.g. when the server is
 * full or the game versions do not match.
 */
public final class S2CConnectionRejectedPacket {

	private String message;

	public S2CConnectionRejectedPacket() {
		// default public constructor
	}

	public S2CConnectionRejectedPacket(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
