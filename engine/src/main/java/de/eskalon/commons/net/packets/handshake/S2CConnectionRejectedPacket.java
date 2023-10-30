package de.eskalon.commons.net.packets.handshake;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This message is sent if the server rejects a client, e.g. when the server is
 * full or the game versions do not match.
 */
@AllArgsConstructor
@NoArgsConstructor
public final class S2CConnectionRejectedPacket {

	private @Getter String message;

}
