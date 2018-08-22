package de.gg.event;

import java.util.HashMap;

import de.gg.game.data.GameSessionSetup;
import de.gg.network.LobbyPlayer;

/**
 * Is posted when the client connected to the server successfully.
 *
 * @see ConnectionFailedEvent
 */
public class ConnectionEstablishedEvent {

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

	public ConnectionEstablishedEvent(HashMap<Short, LobbyPlayer> players,
			short clientId, GameSessionSetup settings) {
		this.players = players;
		this.settings = settings;
		this.clientId = clientId;
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

}
