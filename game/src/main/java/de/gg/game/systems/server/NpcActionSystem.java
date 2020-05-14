package de.gg.game.systems.server;

import de.gg.game.model.entities.Character;
import de.gg.game.network.rmi.AuthoritativeResultListener;

public class NpcActionSystem extends NpcSystem {

	public NpcActionSystem(AuthoritativeResultListener resultListener) {
		super(resultListener, 2400, false);
	}

	@Override
	public void processNPC(short id, Character c) {
		// TODO Amtsbewerbungen
	}

}
