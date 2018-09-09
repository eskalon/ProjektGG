package de.gg.network.messages;

public class ClientSetupMessage {

	/**
	 * The hostname of the client's machine.
	 */
	private String hostname;

	public ClientSetupMessage() {
		// default public constructor
	}

	public ClientSetupMessage(String hostname) {
		this.hostname = hostname;
	}

	public String getHostname() {
		return hostname;
	}

}
