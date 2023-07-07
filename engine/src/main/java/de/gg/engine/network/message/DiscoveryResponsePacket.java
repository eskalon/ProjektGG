package de.gg.engine.network.message;

/**
 * This packet is sent to the client by a discovered game server.
 */
public class DiscoveryResponsePacket {

	/**
	 * The TCP port the server is running on.
	 */
	private int port;
	/**
	 * The current player count.
	 */
	private int playerCount;
	/**
	 * The maximum number of players.
	 */
	private int maxPlayerCount;
	/**
	 * The game's name.
	 */
	private String gameName;

	public DiscoveryResponsePacket() {
		// default public constructor
	}

	public DiscoveryResponsePacket(int port, String gameName, int playerCount,
			int maxPlayerCount) {
		this.port = port;
		this.gameName = gameName;
		this.playerCount = playerCount;
		this.maxPlayerCount = maxPlayerCount;
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

	public int getMaxPlayerCount() {
		return maxPlayerCount;
	}

}
