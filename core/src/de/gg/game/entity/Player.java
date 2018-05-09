package de.gg.game.entity;

import java.util.ArrayList;
import java.util.List;

import de.gg.game.type.PlayerIcon;
import de.gg.game.type.PlayerTasks.PlayerTask;

public class Player {

	private int availableAp;
	private short currentlyPlayedCharacterId;
	private List<Profession> learnedProfessions = new ArrayList<>();
	private PlayerIcon icon;

	/**
	 * A list of the ids of all building slots this player owns.
	 */
	private List<Short> ownedBuidings = new ArrayList<>();
	/**
	 * The monetary value of stuff inherited in this round. Is reseted after the
	 * end round calculations.
	 */
	private int previouslyInheritedValue = 0;

	/**
	 * Whether this character is currently ill.
	 */
	private boolean ill = false;

	private PlayerSkillSet skills = new PlayerSkillSet();

	private PlayerTask currentTask = null;
	private int remainingTaskWorkDuration = 0;

	private List<Evidence> evidence = new ArrayList<>();

	private FamilyTree family = new FamilyTree();

	public int getAvailableAp() {
		return availableAp;
	}

	public void setAvailableAp(int availableAp) {
		this.availableAp = availableAp;
	}

	public List<Profession> getLearnedProfessions() {
		return learnedProfessions;
	}

	public Character getCurrentlyPlayedCharacter(City city) {
		return city.getCharacters().get(currentlyPlayedCharacterId);
	}

	public short getCurrentlyPlayedCharacterId() {
		return currentlyPlayedCharacterId;
	}

	public void setCurrentlyPlayedCharacterId(
			short currentlyPlayedCharacterId) {
		this.currentlyPlayedCharacterId = currentlyPlayedCharacterId;
	}

	public PlayerIcon getIcon() {
		return icon;
	}

	public void setIcon(PlayerIcon icon) {
		this.icon = icon;
	}

	public PlayerTask getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(PlayerTask currentTask) {
		this.currentTask = currentTask;
	}

	public int getRemainingTaskWorkDuration() {
		return remainingTaskWorkDuration;
	}

	public void setRemainingTaskWorkDuration(int remainingTaskWorkDuration) {
		this.remainingTaskWorkDuration = remainingTaskWorkDuration;
	}

	public List<Evidence> getEvidence() {
		return evidence;
	}

	public List<Short> getOwnedBuidings() {
		return ownedBuidings;
	}

	public int getPreviouslyInheritedValue() {
		return previouslyInheritedValue;
	}

	public void setPreviouslyInheritedValue(int previouslyInheritedValue) {
		this.previouslyInheritedValue = previouslyInheritedValue;
	}

	public boolean isIll() {
		return ill;
	}

	public void setIll(boolean ill) {
		this.ill = ill;
	}

	public FamilyTree getFamily() {
		return family;
	}

	public PlayerSkillSet getSkills() {
		return skills;
	}

	/**
	 * @param city
	 *            The city this player lives in.
	 * @return the overall fortune this player has.
	 */
	public int getFortune(City city) {
		int buildingValue = 0;

		for (short s : ownedBuidings) {
			if (city.getBuildingSlots()[s].isBuiltOn()) {
				buildingValue += city.getBuildingSlots()[s].getBuilding()
						.getValue();
			}
		}

		return city.getCharacters().get(currentlyPlayedCharacterId).getGold()
				+ buildingValue;
	}

}
