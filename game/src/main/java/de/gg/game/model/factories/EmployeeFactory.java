package de.gg.game.model.factories;

import java.util.Random;

import de.eskalon.commons.utils.RandomUtils;
import de.gg.game.model.entities.Employee;

/**
 * This class is responsible for creating the {@link Employee} entities.
 */
public class EmployeeFactory {

	private EmployeeFactory() {
		// not used
	}

	public static Employee createRandomEmployee(Random r) {
		int age = RandomUtils.getInt(r, 16, 70);
		int agilitySkill = RandomUtils.isTrue(r, 2)
				? (RandomUtils.isTrue(r, 3) ? 3 : 2)
				: 1;
		int craftingSkill = RandomUtils.isTrue(r, 2)
				? (RandomUtils.isTrue(r, 3) ? 3 : 2)
				: 1;
		int strengthSkill = RandomUtils.isTrue(r, 2)
				? (RandomUtils.isTrue(r, 3) ? 3 : 2)
				: 1;

		return new Employee("Test Name", age, craftingSkill, agilitySkill,
				strengthSkill);
	}

}