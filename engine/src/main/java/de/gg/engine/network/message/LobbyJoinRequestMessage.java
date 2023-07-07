package de.gg.engine.network.message;

/**
 * This message is sent by a client after a
 * {@linkplain ConnectionEstablishedMessage connection was established}.
 */
public final class LobbyJoinRequestMessage {

	/**
	 * The hostname of the client's machine.
	 */
	private String hostname;
	/**
	 * The game version.
	 */
	public String appVersion;

	public LobbyJoinRequestMessage() {
		// default public constructor
	}

	public LobbyJoinRequestMessage(String hostname, String version) {
		this.hostname = hostname;
		this.appVersion = version;
	}

	public String getHostname() {
		return hostname;
	}

	public String getVersion() {
		return appVersion;
	}

}
