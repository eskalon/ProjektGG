package de.gg.game.events;

/**
 * Is posted when a new chat message is sent. It is also called for one clients
 * own messages.
 */
public class NewChatMessagEvent {

	private String message;
	private short playerId;

	public NewChatMessagEvent() {
	}

	public NewChatMessagEvent(short playerId, String message) {
		this.playerId = playerId;
		this.message = message;
	}

	public short getPlayerId() {
		return playerId;
	}

	public String getMessage() {
		return message;
	}

}
