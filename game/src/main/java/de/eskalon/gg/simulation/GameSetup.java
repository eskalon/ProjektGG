package de.eskalon.gg.simulation;

import java.util.Objects;

import de.eskalon.gg.simulation.model.types.GameDifficulty;
import de.eskalon.gg.simulation.model.types.GameMap;

/**
 * Holds all the information about the settings of a game session. The
 * information should stay constant over the whole match.
 */
public class GameSetup {

	private long seed;
	private GameDifficulty difficulty;
	private GameMap map;

	public GameSetup() {
		// default public constructor
	}

	public GameSetup(GameDifficulty difficulty, GameMap map, long seed) {
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

		final GameSetup other = (GameSetup) obj;
		return Objects.equals(seed, other.seed)
				&& Objects.equals(difficulty, other.difficulty)
				&& Objects.equals(map, other.map);
	}

}
