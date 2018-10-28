package de.gg.engine.network.message;

public class ClientHandshakeMessage {

	/**
	 * The hostname of the client's machine.
	 */
	private String hostname;

	public ClientHandshakeMessage() {
		// default public constructor
	}

	public ClientHandshakeMessage(String hostname) {
		this.hostname = hostname;
	}

	public String getHostname() {
		return hostname;
	}

}
