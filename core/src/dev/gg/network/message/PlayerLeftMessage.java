package dev.gg.network.message;

/**
 * This message is sent after a player leaves a game.
 */
public class PlayerLeftMessage {

	/**
	 * The leaving player's ID.
	 */
	private int id;

	public PlayerLeftMessage() {

	}

	public PlayerLeftMessage(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
