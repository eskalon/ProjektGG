package de.gg.engine.network.message;

public final class ClientHandshakeRequest {

	/**
	 * The hostname of the client's machine.
	 */
	private String hostname;
	/**
	 * The game version.
	 */
	public String version;

	public ClientHandshakeRequest() {
		// default public constructor
	}

	public ClientHandshakeRequest(String hostname, String version) {
		this.hostname = hostname;
		this.version = version;
	}

	public String getHostname() {
		return hostname;
	}
	
	public String getVersion() {
		return version;
	}

}
