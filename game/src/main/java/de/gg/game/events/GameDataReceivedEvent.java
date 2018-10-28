package de.gg.game.events;

import java.util.HashMap;

import javax.annotation.Nullable;

import de.gg.game.network.LobbyPlayer;
import de.gg.game.session.GameSessionSetup;
import de.gg.game.session.SavedGame;

/**
 * Is posted when the client connected to the server successfully.
 *
 * @see ConnectionFailedEvent
 */
public class GameDataReceivedEvent {

	/**
	 * A hashmap of all players and their respective IDs.
	 */
	private HashMap<Short, LobbyPlayer> players;
	/**
	 * The game's settings.
	 */
	private GameSessionSetup sessionSetup;

	private @Nullable SavedGame savedGame;

	public GameDataReceivedEvent(HashMap<Short, LobbyPlayer> players,
			GameSessionSetup sessionSetup, @Nullable SavedGame savedGame) {
		this.players = players;
		this.sessionSetup = sessionSetup;
		this.savedGame = savedGame;
	}

	public HashMap<Short, LobbyPlayer> getPlayers() {
		return players;
	}

	public GameSessionSetup getSessionSetup() {
		return sessionSetup;
	}

	public SavedGame getSavedGame() {
		return savedGame;
	}

}
