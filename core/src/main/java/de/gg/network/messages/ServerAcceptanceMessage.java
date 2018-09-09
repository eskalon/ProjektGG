package de.gg.network.messages;

/**
 * This message is sent to the client if the server accepts its connection.
 */
public class ServerAcceptanceMessage {

	/**
	 * The version of the server.
	 */
	public String serverVersion;

	public ServerAcceptanceMessage() {
		// default public constructor
	}

	public ServerAcceptanceMessage(String serverVersion) {
		super();
		this.serverVersion = serverVersion;
	}

	public String getServerVersion() {
		return serverVersion;
	}

}
