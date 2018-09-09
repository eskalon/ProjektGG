package de.gg.game.systems.server;

import de.gg.game.entities.Character;
import de.gg.network.rmi.AuthoritativeResultListener;

public class NpcActionSystem2 extends NpcSystem {

	public NpcActionSystem2(AuthoritativeResultListener resultListener) {
		super(resultListener);
	}

	@Override
	public void processNPC(short id, Character c) {
		// TODO Anträge, etc.
	}

	@Override
	public int getTickRate() {
		return 3000;
	}

	@Override
	public boolean isProcessedContinuously() {
		return false;
	}

}
