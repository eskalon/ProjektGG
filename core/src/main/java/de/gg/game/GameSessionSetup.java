package de.gg.game;

import java.util.Objects;

import de.gg.game.types.GameDifficulty;
import de.gg.game.types.GameMap;

/**
 * Holds all the information about the settings of a game session. The
 * information should stay constant over the whole match.
 */
public class GameSessionSetup {

	/**
	 * The random seed used by the game. Needed to synchronize the random events
	 * of all clients. Should not change after it is set.
	 */
	private long seed;
	/**
	 * The game difficulty. Should not change after it is set.
	 */
	private GameDifficulty difficulty;
	/**
	 * The map id. Should not change after it is set.
	 */
	private GameMap map;

	public GameSessionSetup() {
	}

	public GameSessionSetup(GameDifficulty difficulty, GameMap map, long seed) {
		this.difficulty = difficulty;
		this.map = map;
		this.seed = seed;
	}

	public long getSeed() {
		return seed;
	}

	public GameDifficulty getDifficulty() {
		return difficulty;
	}

	/**
	 * @return The used map.
	 */
	public GameMap getMap() {
		return map;
	}

	@Override
	public int hashCode() {
		return Objects.hash(seed, difficulty, map);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		final GameSessionSetup other = (GameSessionSetup) obj;
		return Objects.equals(seed, other.seed)
				&& Objects.equals(difficulty, other.difficulty)
				&& Objects.equals(map, other.map);
	}

}
