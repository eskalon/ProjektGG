package de.gg.game.systems.server;

import java.util.Random;

import de.eskalon.commons.utils.RandomUtils;
import de.gg.game.model.World;
import de.gg.game.model.entities.Player;
import de.gg.game.network.rmi.AuthoritativeResultListener;

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
			if (RandomUtils.isTrue(random, 6)) { // Recuperation
				p.setIll(false);

				resultListener.onPlayerIllnessChange(id, false);
			}
		} else { // Infection
			if (RandomUtils.isTrue(random, 90)) {
				p.setIll(true);

				resultListener.onPlayerIllnessChange(id, true);
			}
		}

		// AP
		// if(p.getAvailableAp() > 17)
		// TODO remove some aps
	}

}
