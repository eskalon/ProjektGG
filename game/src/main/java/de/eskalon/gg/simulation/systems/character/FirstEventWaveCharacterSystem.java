package de.eskalon.gg.simulation.systems.character;

import java.util.Random;

import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.entities.Character;
import de.eskalon.gg.simulation.model.entities.Player;
import de.eskalon.gg.simulation.systems.AbstractScheduledProcessingSystem;

public class FirstEventWaveCharacterSystem
		extends AbstractScheduledProcessingSystem<Character> {

	private World world;
	private Random random;

	public FirstEventWaveCharacterSystem(World world) {
		super(101);
		this.world = world;
		this.random = new Random(world.getSeed());
	}

	@Override
	public void process(short id, Character c) {
		/* DEATH */
		if (c.getHp() <= 0) {
			// a) Player characters:
			Player p = world.getPlayerByCharacterId(id);
			if (p != null) {
				// TODO change character, inheritance, illness reset, family
				// reset
			}

			// b) Player siblings:
			// TODO inheritance stuff

			// c) Other characters:
			// TODO replace character by random new one

			// d) For everyone:
			// TODO reset positions, remove character from list

			return;
		}
	}

}
