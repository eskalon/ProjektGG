package de.gg.game.system.server;

import java.util.Random;

import de.gg.game.entity.Character;
import de.gg.game.entity.City;
import de.gg.game.system.ProcessingSystem;
import de.gg.util.RandomUtils;

public class HealthAndDamageSystem extends ProcessingSystem<Character> {

	private Random random;

	@Override
	public void init(City city, long seed) {
		this.random = new Random(seed);
	}

	@Override
	public void process(Character c) {
		// Apply damage for age
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

		// Illness
		if (c.isIll()) {
			if (RandomUtils.rollTheDice(random, 5)) { // Genesung
				c.setIll(false);
			}
		} else { // Erkrankung
			if (RandomUtils.rollTheDice(random, 100)) {
				c.setIll(true);
			}
		}

		// TODO death
	}

	@Override
	public boolean isProcessedContinuously() {
		return false;
	}

	@Override
	public int getTickRate() {
		return 600;
	}

}
