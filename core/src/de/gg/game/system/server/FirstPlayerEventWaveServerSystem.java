package de.gg.game.system.server;

import java.util.Random;

import de.gg.game.AuthoritativeSession;
import de.gg.game.entity.City;
import de.gg.game.entity.Player;
import de.gg.util.RandomUtils;

/**
 * This system processes after 60 seconds and takes care of the first wave of
 * events on the server side for players.
 */
public class FirstPlayerEventWaveServerSystem
		extends ServerProcessingSystem<Player> {

	private Random random;

	public FirstPlayerEventWaveServerSystem(
			AuthoritativeSession serverSession) {
		super(serverSession);
	}

	@Override
	public void init(City city, long seed) {
		this.random = new Random(seed);
	}

	@Override
	public void process(short id, Player p) {
		// ILLNESS
		if (p.isIll()) {
			if (RandomUtils.rollTheDice(random, 6)) { // Genesung
				p.setIll(false);

				serverSession.getResultListenerStub().onPlayerIllnessChange(id,
						false);
			}
		} else { // Erkrankung
			if (RandomUtils.rollTheDice(random, 90)) {
				p.setIll(true);

				serverSession.getResultListenerStub().onPlayerIllnessChange(id,
						true);
			}
		}
	}

	@Override
	public boolean isProcessedContinuously() {
		return false;
	}

	@Override
	public int getTickRate() {
		return 602;
	}

}
