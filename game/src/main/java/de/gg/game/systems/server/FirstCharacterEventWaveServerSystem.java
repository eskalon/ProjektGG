package de.gg.game.systems.server;

import de.gg.game.model.World;
import de.gg.game.model.entities.Character;
import de.gg.game.model.entities.Player;
import de.gg.game.network.rmi.AuthoritativeResultListener;

/**
 * This system processes after 60 seconds and takes care of the first wave of
 * events on the server side for characters.
 */
public class FirstCharacterEventWaveServerSystem
		extends ServerProcessingSystem<Character> {

	// private Random random;
	private World world;

	public FirstCharacterEventWaveServerSystem(
			AuthoritativeResultListener resultListener) {
		super(resultListener, 600, false);
	}

	@Override
	public void init(World world, long seed) {
		// this.random = new Random(seed);
		this.world = world;
	}

	@Override
	public void process(short id, Character c) {
		// DEATH
		if (c.getHp() <= 0) {
			// a) Player characters:
			Player p = world.getPlayerByCharacterId(id);
			if (p != null) {
				// TODO Charakter tauschen, Erbe, Illness-Reset, Family-Reset
			}

			// b) Player siblings:
			// TODO Erbe

			// c) Other characters:
			// TODO replace character by random new one

			// d) For everyone:
			// TODO Ämter resetten, Character aus Liste entfernen

			resultListener.onCharacterDeath(id);

			return;
		}
	}

}
