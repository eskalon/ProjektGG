package de.gg.event;

import java.util.HashMap;

import dev.gg.core.LobbyPlayer;
import dev.gg.data.GameSessionSetup;

public class GameSessionSetupEvent {

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

	public GameSessionSetupEvent() {
	}

	public GameSessionSetupEvent(HashMap<Short, LobbyPlayer> players, short clientId,
			GameSessionSetup settings) {
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
