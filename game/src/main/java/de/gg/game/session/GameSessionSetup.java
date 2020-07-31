package de.gg.game.session;

import java.util.Objects;

import de.gg.game.model.types.GameDifficulty;
import de.gg.game.model.types.GameMap;

/**
 * Holds all the information about the settings of a game session. The
 * information should stay constant over the whole match.
 */
public class GameSessionSetup {

	/**
	 * The random seed used by the game. Is needed to synchronize the random
	 * events of all clients. Should not change after it is set.
	 */
	private long seed;
	/**
	 * The game's difficulty. Should not change after it is set.
	 */
	private GameDifficulty difficulty;
	/**
	 * The used map. Should not change after it is set.
	 */
	private GameMap map;

	public GameSessionSetup() {
	}

	public GameSessionSetup(GameDifficulty difficulty, GameMap map, long seed) {
		this.difficulty = difficulty;
		this.map = map;
		this.seed = seed;
	}

	/**
	 * @return the random seed used by the game. Is needed to synchronize the
	 *         random events of all clients.
	 */
	public long getSeed() {
		return seed;
	}

	/**
	 * @return the game's difficulty.
	 */
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
