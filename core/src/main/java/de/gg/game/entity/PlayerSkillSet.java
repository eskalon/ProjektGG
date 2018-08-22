package de.gg.game.entity;

/**
 * Holds information about the skills a player can have.
 */
public class PlayerSkillSet {

	private int agilitySkill;
	private int bargainSkill;
	private int craftingSkill;
	private int combatSkill;
	private int rhetoricalSkill;
	private int stealthSkill;

	public PlayerSkillSet() {
		// default public constructor
	}

	public PlayerSkillSet(int agilitySkill, int bargainSkill, int craftingSkill,
			int combatSkill, int rhetoricalSkill, int stealthSkill) {
		this.agilitySkill = agilitySkill;
		this.bargainSkill = bargainSkill;
		this.craftingSkill = craftingSkill;
		this.combatSkill = combatSkill;
		this.rhetoricalSkill = rhetoricalSkill;
		this.stealthSkill = stealthSkill;
	}

	public int getAgilitySkill() {
		return agilitySkill;
	}

	public void setAgilitySkill(int agilitySkill) {
		this.agilitySkill = agilitySkill;
	}

	public int getBargainSkill() {
		return bargainSkill;
	}

	public void setBargainSkill(int bargainSkill) {
		this.bargainSkill = bargainSkill;
	}

	public int getCraftingSkill() {
		return craftingSkill;
	}

	public void setCraftingSkill(int craftingSkill) {
		this.craftingSkill = craftingSkill;
	}

	public int getCombatSkill() {
		return combatSkill;
	}

	public void setCombatSkill(int combatSkill) {
		this.combatSkill = combatSkill;
	}

	public int getRhetoricalSkill() {
		return rhetoricalSkill;
	}

	public void setRhetoricalSkill(int rhetoricalSkill) {
		this.rhetoricalSkill = rhetoricalSkill;
	}

	public int getStealthSkill() {
		return stealthSkill;
	}

	public void setStealthSkill(int stealthSkill) {
		this.stealthSkill = stealthSkill;
	}
}