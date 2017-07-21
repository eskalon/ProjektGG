package dev.gg.network.message;

import java.util.HashMap;

import dev.gg.core.GameSession.GameDifficulty;
import dev.gg.network.Player;

/**
 * This message is the first thing sent to the client by the server and contains
 * all the necessary information about the game.
 */
public class GameSetupMessage {

	/**
	 * A hashmap of all players and their respective IDs.
	 */
	private HashMap<Short, Player> players;
	/**
	 * The ID of the client player.
	 */
	private short clientId;
	/**
	 * The random seed used by the game. Needed to synchronize the random events
	 * of all clients.
	 */
	private long seed;
	/**
	 * The game difficulty.
	 */
	private GameDifficulty difficulty;

	public GameSetupMessage() {
	}

	public GameSetupMessage(HashMap<Short, Player> players,
			GameDifficulty difficulty, short clientId, long seed) {
		this.players = players;
		this.difficulty = difficulty;
		this.clientId = clientId;
		this.seed = seed;
	}

	public HashMap<Short, Player> getPlayers() {
		return players;
	}

	public short getId() {
		return clientId;
	}

	public long getSeed() {
		return seed;
	}

	public GameDifficulty getDifficulty() {
		return difficulty;
	}

}
