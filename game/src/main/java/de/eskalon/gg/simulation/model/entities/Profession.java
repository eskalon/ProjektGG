package de.eskalon.gg.simulation.model.entities;

import de.eskalon.commons.lang.ILocalizable;
import de.eskalon.gg.simulation.model.types.PlayerTaskType;
import de.eskalon.gg.simulation.model.types.ProfessionType;

public final class Profession implements ILocalizable {

	private ProfessionType profession;
	/**
	 * If the player got enough experience in a profession he can level up.
	 * Starts with <code>1</code>.
	 *
	 * @see PlayerTaskType#UPGRADING_MASTER
	 */
	private int level;
	private int experience;

	public Profession() {
		// default public constructor
	}

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

	@Override
	public String getUnlocalizedName() {
		return profession.getUnlocalizedName() + level;
	}

}
