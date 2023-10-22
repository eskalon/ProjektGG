package de.eskalon.gg.simulation.systems.character;

import java.util.Map.Entry;

import de.eskalon.gg.simulation.model.entities.Character;
import de.eskalon.gg.simulation.model.types.PositionType;
import de.eskalon.gg.simulation.systems.AbstractScheduledProcessingSystem;

public class RoundStartCharacterSystem extends
		AbstractScheduledProcessingSystem<de.eskalon.gg.simulation.model.entities.Character> {

	public RoundStartCharacterSystem() {
		super(0);
	}

	@Override
	public void process(short id, Character c) {
		// AGE
		c.setAge(c.getAge() + 1);

		// AGE DAMAGE
		if (c.getAge() > 18) {
			if (c.getAge() > 45) {
				if (c.getAge() > 65) {
					// > 65
					c.setHp(c.getHp() - 3);
				} else {
					// 45-65
					c.setHp(c.getHp() - 2);
				}
			} else {
				// 19 - 45
				c.setHp(c.getHp() - 1);
			}
		}

		// SALARY
		PositionType position = c.getPosition();
		if (position != null) {
			c.setGold(c.getGold() + position.getSalary());
		}

		// TEMPORARY OPINION MODIFIERS
		for (Entry<Short, Integer> opinionEntry : c.getOpinionModifiers()
				.entrySet()) {
			if (opinionEntry.getValue() > 0) {
				opinionEntry.setValue(opinionEntry.getValue() - 4);
				if (opinionEntry.getValue() < 0)
					c.getOpinionModifiers().remove(opinionEntry.getKey());
			}
			if (opinionEntry.getValue() < 0) {
				opinionEntry.setValue(opinionEntry.getValue() + 3);
				if (opinionEntry.getValue() > 0)
					c.getOpinionModifiers().remove(opinionEntry.getKey());
			}
		}

		// REPUTATION MODIFIERS
		if (c.getReputationModifiers() > 0)
			c.setReputationModifiers(c.getReputationModifiers() - 1);
		if (c.getReputationModifiers() < 0)
			c.setReputationModifiers(c.getReputationModifiers() + 1);
	}

}
