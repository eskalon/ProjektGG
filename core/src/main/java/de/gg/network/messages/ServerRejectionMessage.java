package de.gg.network.messages;

/**
 * A child class of this message is sent if the server rejects a client.
 */
public class ServerRejectionMessage {

	private String message;

	public ServerRejectionMessage() {
		// default public constructor
	}

	public ServerRejectionMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
