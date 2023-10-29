package de.eskalon.commons.net.packets.handshake;

/**
 * This message is sent by a client after a
 * {@linkplain S2CConnectionEstablishedPacket connection was established}.
 */
public final class C2SRequestJoiningLobbyPacket {

	/**
	 * The hostname of the client's machine.
	 */
	private String hostname;
	/**
	 * The game version.
	 */
	public String appVersion;

	public C2SRequestJoiningLobbyPacket() {
		// default public constructor
	}

	public C2SRequestJoiningLobbyPacket(String hostname, String version) {
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
