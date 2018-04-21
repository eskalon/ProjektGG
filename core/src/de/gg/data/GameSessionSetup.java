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

	public GameDifficulty getDifficulty() {
		return difficulty;
	}

	/**
	 * @return The index of the used map.
	 */
	public int getMapId() {
		return mapId;
	}

	/**
	 * An enum describing the game difficulty.
	 */
	public enum GameDifficulty {
		EASY(9), NORMAL(4), HARD(-4);

		private float startingGoldMoifier;
		private float actionModifer;
		private float opinionModifer;

		GameDifficulty(float opinionModifer) {
			this.opinionModifer = opinionModifer;
		}

		/**
		 * @return a modifier that is multiplied with the starting gold.
		 */
		public float getStartingGoldMoifier() {
			return startingGoldMoifier;
		}

		/**
		 * This value is applied to the opinion of every character.
		 * 
		 * @return a modifier for the opinion.
		 */
		public float getOpinionModifer() {
			return opinionModifer;
		}

		/**
		 * This value influences the probability a npc does something in favor
		 * of the player.
		 * 
		 * @return a modifier for npc actions.
		 */
		public float getActionModifer() {
			return actionModifer;
		}

	}

}
