package de.gg.event;

import java.io.IOException;
import java.util.HashMap;

import de.gg.data.GameSessionSetup;
import de.gg.network.LobbyPlayer;

/**
 * Is posted when the client is connected to the server.
 */
public class ConnectionEstablishedEvent {

	/**
	 * <i>Not<i> null if a problem occurred while starting the client.
	 */
	private IOException e = null;
	/**
	 * A hashmap of all players and their respective IDs. Null if a problem
	 * occurred while starting the client.
	 */
	private HashMap<Short, LobbyPlayer> players = null;
	/**
	 * The ID of the client player. -1 if a problem occurred while starting
	 * the client.
	 */
	private short clientId = -1;
	/**
	 * The game's settings. Null if a problem occurred while starting the
	 * client.
	 */
	private GameSessionSetup settings = null;

	public ConnectionEstablishedEvent(IOException e) {
		this.e = e;
	}

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

	public IOException getException() {
		return e;
	}

}
