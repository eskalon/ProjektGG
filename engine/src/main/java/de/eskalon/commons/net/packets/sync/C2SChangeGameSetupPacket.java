package de.eskalon.commons.net.packets.sync;

import javax.annotation.Nullable;

/**
 * This message is sent by the hosting client to indicate a change in the game
 * setup, e.g. a map change.
 * 
 * @param <G>
 * @param <S>
 */
public final class C2SChangeGameSetupPacket<G, S> {

	private G sessionSetup;
	private @Nullable S gameState;

	public C2SChangeGameSetupPacket() {
		// default public constructor
	}

	public C2SChangeGameSetupPacket(G sessionSetup, @Nullable S gameState) {
		this.sessionSetup = sessionSetup;
		this.gameState = gameState;
	}

	public G getSessionSetup() {
		return sessionSetup;
	}

	public S getGameState() {
		return gameState;
	}

}
