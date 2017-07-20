package dev.gg.network.message;

import dev.gg.network.Player;

/**
 * This message is sent after a new player joins a game.
 */
public class PlayerJoinedMessage {

	/**
	 * The joining player's ID.
	 */
	private int id;
	/**
	 * The joining player.
	 */
	private Player player;

	public PlayerJoinedMessage() {

	}

	public PlayerJoinedMessage(int id, Player player) {
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
