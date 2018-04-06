package de.gg.data;

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
	private int mapId;

	public GameSessionSetup() {
	}

	public GameSessionSetup(GameDifficulty difficulty, int mapId, long seed) {
		this.difficulty = difficulty;
		this.mapId = mapId;
		this.seed = seed;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public GameDifficulty getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(GameDifficulty difficulty) {
		this.difficulty = difficulty;
	}

	/**
	 * @return The index of the used map.
	 */
	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	/**
	 * An enum describing the game difficulty.
	 */
	public enum GameDifficulty {
		EASY, NORMAL, HARD;

		/**
		 * This modifier is multiplied with the starting gold.
		 */
		private float startingGoldMoifier;
		/**
		 * This value influences the probability a npc does something in favor
		 * of the player.
		 */
		private float actionModifer;
	}

}
