package de.gg.game.systems.server;

import java.util.Random;

import de.gg.game.entities.Player;
import de.gg.game.world.World;
import de.gg.network.rmi.AuthoritativeResultListener;
import de.gg.utils.RandomUtils;

/**
 * This system processes after 60 seconds and takes care of the first wave of
 * events on the server side for players.
 */
public class FirstPlayerEventWaveServerSystem
		extends ServerProcessingSystem<Player> {

	private Random random;

	public FirstPlayerEventWaveServerSystem(
			AuthoritativeResultListener resultListener) {
		super(resultListener, 602, false);
	}

	@Override
	public void init(World world, long seed) {
		this.random = new Random(seed);
	}

	@Override
	public void process(short id, Player p) {
		// ILLNESS
		if (p.isIll()) {
			if (RandomUtils.rollTheDice(random, 6)) { // Genesung
				p.setIll(false);

				resultListener.onPlayerIllnessChange(id, false);
			}
		} else { // Erkrankung
			if (RandomUtils.rollTheDice(random, 90)) {
				p.setIll(true);

				resultListener.onPlayerIllnessChange(id, true);
			}
		}

		// AP
		// if(p.getAvailableAp() > 17)
		// TODO remove some aps
	}

}
