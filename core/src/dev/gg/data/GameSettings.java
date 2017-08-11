package dev.gg.data;

import dev.gg.core.GameSession.GameDifficulty;

/**
 * Holds all the information about the settings of a game session.
 */
public class GameSettings {

	/**
	 * The random seed used by the game. Needed to synchronize the random events
	 * of all clients.
	 */
	private long seed;
	/**
	 * The game difficulty.
	 */
	private GameDifficulty difficulty;

	public GameSettings() {

	}

	public GameSettings(GameDifficulty difficulty, long seed) {
		this.difficulty = difficulty;
		this.seed = seed;
	}

	public long getSeed() {
		return seed;
	}

	public GameDifficulty getDifficulty() {
		return difficulty;
	}

}
