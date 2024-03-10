package de.eskalon.gg.simulation.model.entities;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.types.PlayerIcon;
import de.eskalon.gg.simulation.model.types.PlayerTaskType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public final class Player {

	private @Getter @Setter int availableAP;
	private @Getter @Setter short currentlyPlayedCharacterId;
	private @Getter List<Profession> learnedProfessions = new ArrayList<>();
	private @Getter @Setter PlayerIcon icon;

	/**
	 * A list of the IDs of all building slots this player owns.
	 */
	private @Getter List<Short> ownedBuidings = new ArrayList<>();
	/**
	 * The monetary value of stuff inherited in this round. Is reseted after the
	 * end round tax calculations.
	 */
	private @Getter @Setter int previouslyInheritedValue = 0;

	/**
	 * Whether this character is currently ill.
	 */
	private @Getter @Setter boolean isIll = false;

	private @Getter PlayerSkillSet skills = new PlayerSkillSet();

	private @Getter @Setter @Nullable PlayerTaskType currentTask = null;
	private @Getter @Setter int remainingTaskWorkDuration = 0;

	private @Getter List<Evidence> evidence = new ArrayList<>();

	private @Getter FamilyTree family = new FamilyTree();

	public Character getCurrentlyPlayedCharacter(World world) {
		return world.getCharacters().get(currentlyPlayedCharacterId);
	}

	/**
	 * @param world
	 *            The world this player lives in.
	 * @return the overall fortune this player has.
	 */
	public int getFortune(World world) {
		int buildingValue = 0;

		for (short s : ownedBuidings) {
			if (world.getBuildingSlots()[s].isBuiltOn()) {
				buildingValue += world.getBuildingSlots()[s].getBuilding()
						.getValue();
			}
		}

		return world.getCharacters().get(currentlyPlayedCharacterId).getGold()
				+ buildingValue;
	}

}
