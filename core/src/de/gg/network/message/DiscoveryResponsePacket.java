package de.gg.network.message;

/**
 * This packet is sent to the client after he discovered a game server.
 */
public class DiscoveryResponsePacket {

	/**
	 * The tcp port the server is running on.
	 */
	private int port;
	/**
	 * The current player count.
	 */
	private int playerCount;
	/**
	 * The game's name.
	 */
	private String gameName;

	public DiscoveryResponsePacket() {
	}

	public DiscoveryResponsePacket(int port, String gameName, int playerCount) {
		this.port = port;
		this.gameName = gameName;
		this.playerCount = playerCount;
	}

	public int getPort() {
		return port;
	}

	public String getGameName() {
		return gameName;
	}

	public int getPlayerCount() {
		return playerCount;
	}

}
