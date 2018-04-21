package de.gg.game.system.server;

import de.gg.game.AuthoritativeSession;
import de.gg.game.system.ProcessingSystem;

public abstract class ServerProcessingSystem<E> extends ProcessingSystem<E> {

	protected AuthoritativeSession serverSession;

	public ServerProcessingSystem(AuthoritativeSession serverSession) {
		this.serverSession = serverSession;
	}

}
