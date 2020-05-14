package de.gg.game.systems.server;

import de.gg.game.model.entities.Character;
import de.gg.game.network.rmi.AuthoritativeResultListener;

public class NpcActionSystem2 extends NpcSystem {

	public NpcActionSystem2(AuthoritativeResultListener resultListener) {
		super(resultListener, 3000, false);
	}

	@Override
	public void processNPC(short id, Character c) {
		// TODO Anträge, etc.
	}

}
