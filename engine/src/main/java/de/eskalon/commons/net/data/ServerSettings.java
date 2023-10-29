package de.eskalon.commons.net.data;

import java.util.HashMap;

import javax.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ServerSettings {

	private @Getter @Setter String gameName;
	private @Getter @Setter int maxPlayerCount;
	/**
	 * The server's TCP port.
	 */
	private @Getter @Setter int port;
	/**
	 * Whether the game server should broadcast this game on the local network.
	 */
	private @Getter @Setter boolean isPublic;
	/**
	 * The application's version.
	 */
	private @Getter @Setter String version;
	/**
	 * Whether only the host can issue commands.
	 */
	private @Getter @Setter boolean hostOnlyCommands;
	private @Getter @Setter @Nullable HashMap<Short, String> savedClientIdentifiers;

}
