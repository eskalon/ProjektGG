package de.gg.game.entity;

import de.gg.game.type.ItemTypes.ItemType;

public class Employee {

	private int age;

	private int craftingSkill;
	private int agilitySkill;
	private int strengthSkill;

	private ItemType itemInProduction = null;
	private int productionProgress = 0;

	public Employee() {
		// default public constructor
	}

	public Employee(int age, int craftingSkill, int agilitySkill,
			int strengthSkill) {
		this.age = age;
		this.craftingSkill = craftingSkill;
		this.agilitySkill = agilitySkill;
		this.strengthSkill = strengthSkill;
	}

	public int getCraftingSkill() {
		return craftingSkill;
	}

	public void setCraftingSkill(int craftingSkill) {
		this.craftingSkill = craftingSkill;
	}

	public int getAgilitySkill() {
		return agilitySkill;
	}

	public void setAgilitySkill(int agilitySkill) {
		this.agilitySkill = agilitySkill;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getStrengthSkill() {
		return strengthSkill;
	}

	public void setStrengthSkill(int strengthSkill) {
		this.strengthSkill = strengthSkill;
	}

	/**
	 * @return The item the employee is currently producing.
	 */
	public ItemType getItemInProduction() {
		return itemInProduction;
	}

	public void setItemInProduction(ItemType itemInProduction) {
		this.itemInProduction = itemInProduction;
	}

	public int getProductionProgress() {
		return productionProgress;
	}

	public void setProductionProgress(int productionProgress) {
		this.productionProgress = productionProgress;
	}

}
