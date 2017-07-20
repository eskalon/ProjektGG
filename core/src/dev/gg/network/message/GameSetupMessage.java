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
	private HashMap<Integer, Player> players;
	/**
	 * The ID of the client player.
	 */
	private int id;
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

	public GameSetupMessage(HashMap<Integer, Player> players,
			GameDifficulty difficulty, int id, long seed) {
		this.players = players;
		this.difficulty = difficulty;
		this.id = id;
		this.seed = seed;
	}

	public HashMap<Integer, Player> getPlayers() {
		return players;
	}

	public int getId() {
		return id;
	}

	public long getSeed() {
		return seed;
	}

	public GameDifficulty getDifficulty() {
		return difficulty;
	}

}
