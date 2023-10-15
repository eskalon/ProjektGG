package de.eskalon.commons.net.packets.sync;

/**
 * This message is sent by a client to indicate a change of their own player
 * data.
 * 
 * @param <P>
 */
public final class ChangePlayerPacket<P> {

	private P playerData;

	public ChangePlayerPacket() {
		// default public constructor
	}

	public ChangePlayerPacket(P playerData) {
		this.playerData = playerData;
	}

	public P getPlayerData() {
		return playerData;
	}

}
