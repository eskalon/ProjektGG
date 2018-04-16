package de.gg.entity;

import java.util.Random;

import de.gg.entity.ItemTypes.ItemType;
import de.gg.util.RandomUtils;

public class Employee {

	private int age;

	private int craftingSkill;
	private int agilitySkill;
	private int strengthSkill;

	private ItemType itemInProduction = null;
	private int productionProgress = 0;

	public static Employee getRandomEmployee(Random r) {
		Employee e = new Employee();
		e.age = RandomUtils.getRandomNumber(r, 16, 70);
		e.agilitySkill = RandomUtils.rollTheDice(r, 2)
				? (RandomUtils.rollTheDice(r, 3) ? 3 : 2)
				: 1;
		e.craftingSkill = RandomUtils.rollTheDice(r, 2)
				? (RandomUtils.rollTheDice(r, 3) ? 3 : 2)
				: 1;
		e.strengthSkill = RandomUtils.rollTheDice(r, 2)
				? (RandomUtils.rollTheDice(r, 3) ? 3 : 2)
				: 1;

		return e;
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
