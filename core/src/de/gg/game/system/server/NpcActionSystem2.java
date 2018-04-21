package de.gg.game.system.server;

import de.gg.game.AuthoritativeSession;
import de.gg.game.entity.Character;

public class NpcActionSystem2 extends ServerProcessingSystem<Character> {

	public NpcActionSystem2(AuthoritativeSession serverSession) {
		super(serverSession);
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
