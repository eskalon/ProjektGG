package de.eskalon.commons.net.packets.sync;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This message is sent by a client to indicate a change of their own player
 * data.
 * 
 * @param <P>
 */
@AllArgsConstructor
@NoArgsConstructor
public final class C2SChangePlayerPacket<P> {

	private @Getter P playerData;

}
