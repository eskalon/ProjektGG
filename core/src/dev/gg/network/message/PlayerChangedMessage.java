package dev.gg.network.message;

import dev.gg.network.Player;

/**
 * This message is sent when a player was changed.
 */
public class PlayerChangedMessage {

	/**
	 * The changed player's ID.
	 */
	private int id;
	/**
	 * The changed player.
	 */
	private Player player;

	public PlayerChangedMessage() {

	}

	public PlayerChangedMessage(int id, Player player) {
		this.id = id;
		this.player = player;
	}

	public int getId() {
		return id;
	}

	public Player getPlayer() {
		return player;
	}

}
