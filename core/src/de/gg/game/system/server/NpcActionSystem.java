package de.gg.game.system.server;

import de.gg.game.AuthoritativeSession;
import de.gg.game.entity.Character;

public class NpcActionSystem extends ServerProcessingSystem<Character> {

	public NpcActionSystem(AuthoritativeSession serverSession) {
		super(serverSession);
	}

	@Override
	public void process(short id, Character c) {
		if (c.getNPCTrait() != null) {
			// TODO Amtsbewerbungen
		}
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
