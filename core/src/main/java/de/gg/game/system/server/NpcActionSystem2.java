package de.gg.game.system.server;

import de.gg.game.entity.Character;
import de.gg.network.rmi.AuthoritativeResultListener;

public class NpcActionSystem2 extends ServerProcessingSystem<Character> {

	public NpcActionSystem2(AuthoritativeResultListener resultListener) {
		super(resultListener);
	}

	@Override
	public void process(short id, Character c) {
		if (c.getNPCTrait() != null) {
			// TODO Anträge, etc.
		}
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
