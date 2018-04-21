package de.gg.game.entity;

import java.util.ArrayList;
import java.util.List;

import de.gg.game.entity.PlayerTasks.PlayerTask;

public class Player {

	private int availableAp;
	private Character currentlyPlayedCharacter;
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
	private int previouslyInheritedValue;

	/**
	 * Whether this character is currently ill.
	 */
	private boolean ill;

	private PlayerSkillSet skills;

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

	public void setLearnedProfessions(List<Profession> learnedProfessions) {
		this.learnedProfessions = learnedProfessions;
	}

	public Character getCurrentlyPlayedCharacter() {
		return currentlyPlayedCharacter;
	}

	public void setCurrentlyPlayedCharacter(
			Character currentlyPlayedCharacter) {
		this.currentlyPlayedCharacter = currentlyPlayedCharacter;
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

	public void setEvidence(List<Evidence> evidence) {
		this.evidence = evidence;
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

		return currentlyPlayedCharacter.getGold() + buildingValue;
	}

	public enum PlayerIcon {
		ICON_1("72A0C1FF", "icon_1"), ICON_2("9F2B68FF",
				"icon_2"), ICON_3("FFBF00FF", "icon_1");

		private String color;
		private String iconFileName;

		private PlayerIcon(String color, String iconFileName) {
			this.color = color;
			this.iconFileName = iconFileName;
		}

		public String getColor() {
			return color;
		}

		public String getIconFileName() {
			return iconFileName;
		}

	}

	/**
	 * Represents a family hierarchy.
	 */
	public class FamilyTree {

		private short fatherCharacterId = -1, motherCharacterId = -1;
		private List<Short> childrenCharacterIds = new ArrayList<>();

		public short getFatherCharacterId() {
			return fatherCharacterId;
		}

		public void setFatherCharacterId(short fatherCharacterId) {
			this.fatherCharacterId = fatherCharacterId;
		}

		public short getMotherCharacterId() {
			return motherCharacterId;
		}

		public void setMotherCharacterId(short motherCharacterId) {
			this.motherCharacterId = motherCharacterId;
		}

		public List<Short> getChildrenCharacterIds() {
			return childrenCharacterIds;
		}

	}

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

		public PlayerSkillSet(int agilitySkill, int bargainSkill,
				int craftingSkill, int combatSkill, int rhetoricalSkill,
				int stealthSkill) {
			super();
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

}
