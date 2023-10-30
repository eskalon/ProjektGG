package de.eskalon.commons.net.packets.handshake;

import de.eskalon.commons.net.packets.data.LobbyData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This message is sent upon a {@link C2SRequestJoiningLobbyPacket} if the
 * server accepts the request.
 */
@AllArgsConstructor
@NoArgsConstructor
public final class S2CLobbyJoinedPacket {

	private @Getter short clientNetworkId;
	private @Getter LobbyData lobbyData;

}
