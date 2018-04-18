package de.gg.game.entity;

import java.util.List;

import de.gg.game.entity.PlayerTasks.PlayerTask;

public class Player {

	private int availableAp;
	private List<Profession> learnedProfessions;
	private Character currentlyPlayedCharacter;
	private PlayerIcon icon;

	private int agilitySkill;
	private int bargainSkill;
	private int craftingSkill;
	private int combatSkill;
	private int rhetoricalSkill;
	private int stealthSkill;

	private PlayerTask currentTask;
	private int remainingTaskWorkDuration;

	private List<Evidence> evidence;

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

}
