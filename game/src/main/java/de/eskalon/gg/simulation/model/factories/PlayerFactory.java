package de.eskalon.gg.simulation.model.factories;

import de.eskalon.gg.simulation.model.entities.Player;
import de.eskalon.gg.simulation.model.entities.Profession;
import de.eskalon.gg.simulation.model.types.PlayerIcon;

/**
 * This class is responsible for creating the {@link Player} entities.
 */
public class PlayerFactory {

	private PlayerFactory() {
		// not used
	}

	/**
	 * Creates a player.
	 *
	 * @param characterId
	 * @param icon
	 * @param profession
	 * @param residenceIndex
	 * @param productionBuildingIndex
	 * @param agilitySkill
	 * @param bargainSkill
	 * @param combatSkill
	 * @param craftingSkill
	 * @param rhetoricalSkill
	 * @param stealthSkill
	 * @return the player entity.
	 */
	public static Player createPlayerCharacter(short characterId,
			PlayerIcon icon, Profession profession, short residenceIndex,
			short productionBuildingIndex, int agilitySkill, int bargainSkill,
			int combatSkill, int craftingSkill, int rhetoricalSkill,
			int stealthSkill) {
		Player p = new Player();
		p.setAvailableAP(4);
		p.setCurrentlyPlayedCharacterId(characterId);
		p.setIcon(icon);
		p.getLearnedProfessions().add(profession);
		p.getOwnedBuidings().add(residenceIndex);
		p.getOwnedBuidings().add(productionBuildingIndex);
		p.getSkills().setAgilitySkill(agilitySkill);
		p.getSkills().setBargainSkill(bargainSkill);
		p.getSkills().setCombatSkill(combatSkill);
		p.getSkills().setCraftingSkill(craftingSkill);
		p.getSkills().setRhetoricalSkill(rhetoricalSkill);
		p.getSkills().setStealthSkill(stealthSkill);

		return p;
	}

}
