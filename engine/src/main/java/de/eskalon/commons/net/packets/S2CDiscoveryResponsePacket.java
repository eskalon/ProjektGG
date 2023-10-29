package de.eskalon.commons.net.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This packet is sent to the client by a discovered game server.
 */
@AllArgsConstructor
@NoArgsConstructor
public class S2CDiscoveryResponsePacket {

	/**
	 * The TCP port the server is running on.
	 */
	private @Getter int port;
	/**
	 * The game's name.
	 */
	private @Getter String gameName;
	/**
	 * The current player count.
	 */
	private @Getter int playerCount;
	/**
	 * The maximum number of players.
	 */
	private @Getter int maxPlayerCount;

}
