package dev.gg.network.message;

import dev.gg.core.Player;

/**
 * This message is sent when a player was changed.
 */
public class PlayerChangedMessage {

	/**
	 * The changed player's ID.
	 */
	private short id;
	/**
	 * The changed player.
	 */
	private Player player;

	public PlayerChangedMessage() {

	}

	public PlayerChangedMessage(short id, Player player) {
		this.id = id;
		this.player = player;
	}

	public short getId() {
		return id;
	}

	public Player getPlayer() {
		return player;
	}

}
