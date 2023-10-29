package de.eskalon.gg.simulation.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Holds information about the skills a player can have.
 */
@NoArgsConstructor
@AllArgsConstructor
public final class PlayerSkillSet {

	private @Getter @Setter int agilitySkill;
	private @Getter @Setter int bargainSkill;
	private @Getter @Setter int craftingSkill;
	private @Getter @Setter int combatSkill;
	private @Getter @Setter int rhetoricalSkill;
	private @Getter @Setter int stealthSkill;

}