package de.eskalon.commons.net.packets.handshake;

/**
 * This message is sent by a client after a
 * {@linkplain ConnectionEstablishedPacket connection was established}.
 */
public final class RequestJoiningLobbyPacket {

	/**
	 * The hostname of the client's machine.
	 */
	private String hostname;
	/**
	 * The game version.
	 */
	public String appVersion;

	public RequestJoiningLobbyPacket() {
		// default public constructor
	}

	public RequestJoiningLobbyPacket(String hostname, String version) {
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
