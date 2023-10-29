package de.eskalon.commons.net.packets.sync;

/**
 * This message is sent by a client to indicate a change of their own player
 * data.
 * 
 * @param <P>
 */
public final class C2SChangePlayerPacket<P> {

	private P playerData;

	public C2SChangePlayerPacket() {
		// default public constructor
	}

	public C2SChangePlayerPacket(P playerData) {
		this.playerData = playerData;
	}

	public P getPlayerData() {
		return playerData;
	}

}
