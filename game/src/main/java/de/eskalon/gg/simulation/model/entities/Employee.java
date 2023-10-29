package de.eskalon.gg.simulation.model.entities;

import javax.annotation.Nullable;

import de.eskalon.gg.simulation.model.types.ItemType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public final class Employee {

	private @Getter String fullName;
	private @Getter @Setter int age;

	private @Getter @Setter int craftingSkill;
	private @Getter @Setter int agilitySkill;
	private @Getter @Setter int strengthSkill;

	/**
	 * The item the employee is currently producing.
	 */
	private @Getter @Setter @Nullable ItemType itemInProduction = null;
	private @Getter @Setter int productionProgress = 0;

	public Employee(String fullName, int age, int craftingSkill,
			int agilitySkill, int strengthSkill) {
		this.fullName = fullName;
		this.age = age;
		this.craftingSkill = craftingSkill;
		this.agilitySkill = agilitySkill;
		this.strengthSkill = strengthSkill;
	}

}
