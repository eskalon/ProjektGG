package de.gg.engine.network.message;

/**
 * This message is sent if the server rejects a client, e.g. when the server is
 * full or the game versions do not match.
 */
public final class ConnectionRejectedMessage {

	private String message;

	public ConnectionRejectedMessage() {
		// default public constructor
	}

	public ConnectionRejectedMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
