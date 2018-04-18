package de.gg.game.entity;

import de.gg.game.entity.ProfessionTypes.ProfessionType;

public class Profession {

	private ProfessionType profession;
	/**
	 * If the player got enough experience he can level up.
	 * 
	 * @see PlayerTasks#UPGRADING_MASTER
	 */
	private int level;
	private int experience;

	public ProfessionType getProfession() {
		return profession;
	}

	public void setProfession(ProfessionType profession) {
		this.profession = profession;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

}
