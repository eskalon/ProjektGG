package de.gg.network.message;

public class DiscoveryResponsePacket {

	private int port;
	private int playerCount;
	private String gameName;

	public DiscoveryResponsePacket() {
	}

	public DiscoveryResponsePacket(int port, String gameName,
			int playerCount) {
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
