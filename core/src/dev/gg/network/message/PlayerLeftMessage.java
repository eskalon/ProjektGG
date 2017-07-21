package dev.gg.network.message;

/**
 * This message is sent after a player leaves a game.
 */
public class PlayerLeftMessage {

	/**
	 * The leaving player's ID.
	 */
	private short id;

	public PlayerLeftMessage() {

	}

	public PlayerLeftMessage(short id) {
		this.id = id;
	}

	public short getId() {
		return id;
	}

}
