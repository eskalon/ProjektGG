package de.eskalon.commons.net.packets.handshake;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This message is sent by a client after a
 * {@linkplain S2CConnectionEstablishedPacket connection was established}.
 */
@AllArgsConstructor
@NoArgsConstructor
public final class C2SRequestJoiningLobbyPacket {

	/**
	 * The hostname of the client's machine.
	 */
	private @Getter String hostname;
	/**
	 * The game application's version.
	 */
	public @Getter String version;

}
