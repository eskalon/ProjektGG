package de.gg.network.message;

import java.util.HashMap;

import de.gg.game.SavedGame;
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
	 * The game session's setup.
	 */
	private GameSessionSetup sessionSetup;
	/**
	 * The loaded game session. <code>Null</code> if this match isn't loaded.
	 */
	private SavedGame savedGame;

	public GameSetupMessage() {
		// default public constructor
	}

	public GameSetupMessage(HashMap<Short, LobbyPlayer> players, short clientId,
			GameSessionSetup sessionSetup, SavedGame savedGame) {
		this.players = players;
		this.sessionSetup = sessionSetup;
		this.clientId = clientId;
		this.savedGame = savedGame;
	}

	public HashMap<Short, LobbyPlayer> getPlayers() {
		return players;
	}

	public short getId() {
		return clientId;
	}

	public GameSessionSetup getSessionSetup() {
		return sessionSetup;
	}

	public SavedGame getSavedGame() {
		return savedGame;
	}

}
