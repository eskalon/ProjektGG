package de.gg.engine.network.message;

/**
 * This message is sent if the server rejects a client, e.g. when the server is
 * full.
 */
public final class ServerRejectionResponse {

	private String message;

	public ServerRejectionResponse() {
		// default public constructor
	}

	public ServerRejectionResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
