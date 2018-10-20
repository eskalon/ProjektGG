package de.gg.game.entities;

import java.util.ArrayList;
import java.util.List;

import de.gg.game.types.PlayerIcon;
import de.gg.game.types.PlayerTaskType;
import de.gg.game.world.World;

public class Player {

	private int availableAp;
	private short currentlyPlayedCharacterId;
	private List<Profession> learnedProfessions = new ArrayList<>();
	private PlayerIcon icon;

	private List<Short> ownedBuidings = new ArrayList<>();
	private int previouslyInheritedValue = 0;

	private boolean isIll = false;

	private PlayerSkillSet skills = new PlayerSkillSet();

	private PlayerTaskType currentTask = null;
	private int remainingTaskWorkDuration = 0;

	private List<Evidence> evidence = new ArrayList<>();

	private FamilyTree family = new FamilyTree();

	public Player() {
		// default public constructor
	}

	public int getAvailableAp() {
		return availableAp;
	}

	public void setAvailableAp(int availableAp) {
		this.availableAp = availableAp;
	}

	public List<Profession> getLearnedProfessions() {
		return learnedProfessions;
	}

	public Character getCurrentlyPlayedCharacter(World world) {
		return world.getCharacters().get(currentlyPlayedCharacterId);
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

	public PlayerTaskType getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(PlayerTaskType currentTask) {
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

	/**
	 * @return a list of the IDs of all building slots this player owns.
	 */
	public List<Short> getOwnedBuidings() {
		return ownedBuidings;
	}

	/**
	 * @return the monetary value of stuff inherited in this round. Is reseted
	 *         after the end round tax calculations.
	 */
	public int getPreviouslyInheritedValue() {
		return previouslyInheritedValue;
	}

	public void setPreviouslyInheritedValue(int previouslyInheritedValue) {
		this.previouslyInheritedValue = previouslyInheritedValue;
	}

	/**
	 * @return whether this character is currently ill.
	 */
	public boolean isIll() {
		return isIll;
	}

	public void setIll(boolean ill) {
		this.isIll = ill;
	}

	public FamilyTree getFamily() {
		return family;
	}

	public PlayerSkillSet getSkills() {
		return skills;
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
