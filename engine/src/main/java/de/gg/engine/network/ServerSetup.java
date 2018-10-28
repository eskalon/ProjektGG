package de.gg.engine.network;

public class ServerSetup {

	/**
	 * The application's version.
	 */
	private String version;
	private String gameName;
	private int maxPlayerCount;
	/**
	 * The server's tcp port.
	 */
	private int port;
	/**
	 * Whether the game server should broadcast this game on the local network.
	 */
	private boolean isPublic;
	/**
	 * Whether only the host can issue commands.
	 */
	private boolean hostOnlyCommands;

	public ServerSetup(String gameName, int maxPlayerCount, int port,
			boolean isPublic, String version, boolean hostOnlyCommands) {
		this.gameName = gameName;
		this.maxPlayerCount = maxPlayerCount;
		this.port = port;
		this.isPublic = isPublic;
		this.version = version;
		this.hostOnlyCommands = hostOnlyCommands;
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

	public String getVersion() {
		return version;
	}

	public boolean isHostOnlyCommands() {
		return hostOnlyCommands;
	}

}
