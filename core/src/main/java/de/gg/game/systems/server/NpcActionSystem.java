package de.gg.game.systems.server;

import de.gg.game.entities.Character;
import de.gg.network.rmi.AuthoritativeResultListener;

public class NpcActionSystem extends NpcSystem {

	public NpcActionSystem(AuthoritativeResultListener resultListener) {
		super(resultListener);
	}

	@Override
	public void processNPC(short id, Character c) {
		// TODO Amtsbewerbungen
	}

	@Override
	public int getTickRate() {
		return 2400;
	}

	@Override
	public boolean isProcessedContinuously() {
		return false;
	}

}
