package de.gg.game.system.server;

import de.gg.game.entity.Character;
import de.gg.game.system.ProcessingSystem;

public class IllnessDamageSystem extends ProcessingSystem<Character> {

	@Override
	public void process(Character c) {
		if (c.isIll()) {
			c.setHp(c.getHp() - 1);
		}
	}

	@Override
	public int getTickRate() {
		return 1800; // this system is called two times per round
	}

}
