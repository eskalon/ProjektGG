package de.gg.network;

public class ServerSetup {

	private String gameName;
	private int maxPlayerCount;
	/**
	 * The servers tcp port.
	 */
	private int port;
	/**
	 * Whether the game server should broadcast this game on the local network.
	 */
	private boolean isPublic;

	public ServerSetup(String gameName, int maxPlayerCount, int port,
			boolean isPublic) {
		this.gameName = gameName;
		this.maxPlayerCount = maxPlayerCount;
		this.port = port;
		this.isPublic = isPublic;
	}

	public String getGameName() {
		return gameName;
	}

	public int getMaxPlayerCount() {
		return maxPlayerCount;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public int getPort() {
		return port;
	}

}
