package de.gg.game.entity;

import de.gg.game.type.PlayerTasks;
import de.gg.game.type.ProfessionTypes.ProfessionType;

public class Profession {

	private ProfessionType profession;
	/**
	 * If the player got enough experience he can level up.
	 * 
	 * @see PlayerTasks#UPGRADING_MASTER
	 */
	private int level;
	private int experience;

	public Profession(ProfessionType profession) {
		this(profession, 1, 0);
	}

	public Profession(ProfessionType profession, int level, int experience) {
		this.profession = profession;
		this.level = level;
		this.experience = experience;
	}

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
