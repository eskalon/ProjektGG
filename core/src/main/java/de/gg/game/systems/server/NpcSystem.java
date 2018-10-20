package de.gg.game.systems.server;

import de.gg.game.entities.Character;
import de.gg.network.rmi.AuthoritativeResultListener;

/**
 * This system only processes non-player characters.
 * 
 * @see #processNPC(short, Character)
 */
public abstract class NpcSystem extends ServerProcessingSystem<Character> {

	public NpcSystem(AuthoritativeResultListener resultListener, int tickRate,
			boolean isProcessedContinuously) {
		super(resultListener, tickRate, isProcessedContinuously);
	}

	@Override
	public void process(short id, Character c) {
		if (c.getNPCTrait() != null) { // -> NPC
			processNPC(id, c);
		}
	}

	public abstract void processNPC(short id, Character c);

}
