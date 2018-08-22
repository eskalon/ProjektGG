package de.gg.game.factory;

import java.util.Random;

import de.gg.game.entity.Employee;
import de.gg.util.RandomUtils;

/**
 * This class is responsible for creating the {@link Employee} entities.
 */
public class EmployeeFactory {

	private EmployeeFactory() {
	}

	public static Employee createRandomEmployee(Random r) {
		int age = RandomUtils.getRandomNumber(r, 16, 70);
		int agilitySkill = RandomUtils.rollTheDice(r, 2)
				? (RandomUtils.rollTheDice(r, 3) ? 3 : 2)
				: 1;
		int craftingSkill = RandomUtils.rollTheDice(r, 2)
				? (RandomUtils.rollTheDice(r, 3) ? 3 : 2)
				: 1;
		int strengthSkill = RandomUtils.rollTheDice(r, 2)
				? (RandomUtils.rollTheDice(r, 3) ? 3 : 2)
				: 1;

		return new Employee(age, craftingSkill, agilitySkill, strengthSkill);
	}

}