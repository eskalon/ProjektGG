package de.gg.game.types;

import de.gg.lang.Localizable;

/**
 * An enum describing the game difficulty.
 */
public enum GameDifficulty implements Localizable {
	EASY(9), NORMAL(4), HARD(-4);

	private int additionalStartingGold;
	private float actionModifer;
	private float opinionModifer;

	GameDifficulty(float opinionModifer) {
		this.opinionModifer = opinionModifer;
	}

	/**
	 * @return the additional gold a player gets at the game start.
	 */
	public int getAdditionalStartingGold() {
		return additionalStartingGold;
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
	 * This value influences the probability a npc does something in favor of
	 * the player.
	 *
	 * @return a modifier for npc actions.
	 */
	public float getActionModifer() {
		return actionModifer;
	}

	@Override
	public String getUnlocalizedName() {
		return "type.difficulty." + this.name().toLowerCase() + ".name";
	}

}
