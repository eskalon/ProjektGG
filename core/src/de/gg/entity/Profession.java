package de.gg.entity;

import de.gg.entity.PlayerTasks.PlayerTask;
import de.gg.entity.ProfessionTypes.ProfessionType;

public class Profession {

	private ProfessionType profession;
	/**
	 * If the player got enough experience he can level up.
	 * 
	 * @see PlayerTasks#UPGRADING_MASTER
	 */
	private int level;
	private int experience;

}
