package de.gg.network.message;

import java.util.HashMap;

import de.gg.game.data.GameSessionSetup;
import de.gg.network.LobbyPlayer;

/**
 * This message is the first thing sent to the client by the server and contains
 * all the necessary information about the game.
 */
public class GameSetupMessage {

	/**
	 * A hashmap of all players and their respective IDs.
	 */
	private HashMap<Short, LobbyPlayer> players;
	/**
	 * The ID of the client player.
	 */
	private short clientId;
	/**
	 * The game's settings.
	 */
	private GameSessionSetup settings;
	/**
	 * The version of the game server.
	 */
	private String serverVersion;

	public GameSetupMessage() {
	}

	public GameSetupMessage(HashMap<Short, LobbyPlayer> players, short clientId,
			GameSessionSetup settings, String serverVersion) {
		this.players = players;
		this.settings = settings;
		this.clientId = clientId;
		this.serverVersion = serverVersion;
	}

	public HashMap<Short, LobbyPlayer> getPlayers() {
		return players;
	}

	public short getId() {
		return clientId;
	}

	public GameSessionSetup getSettings() {
		return settings;
	}

	public String getServerVersion() {
		return serverVersion;
	}

}
