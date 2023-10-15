package de.eskalon.commons.net;

import java.util.HashMap;

import javax.annotation.Nullable;

public class ServerSettings {

	/**
	 * The application's version.
	 */
	private String appVersion;
	private String gameName;
	private int maxPlayerCount;
	/**
	 * The server's TCP port.
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

	private @Nullable HashMap<Short, String> savedClientIdentifiers;

	public ServerSettings(String gameName, int maxPlayerCount, int port,
			boolean isPublic, String version, boolean hostOnlyCommands) {
		this.gameName = gameName;
		this.maxPlayerCount = maxPlayerCount;
		this.port = port;
		this.isPublic = isPublic;
		this.appVersion = version;
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
		return appVersion;
	}

	public boolean isHostOnlyCommands() {
		return hostOnlyCommands;
	}

}
