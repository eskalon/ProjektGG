package de.gg.network.message;

/**
 * This message is sent after a chat message got written.
 */
public class ChatMessageSentMessage {

	/**
	 * The sending player's ID.
	 */
	private short senderId;
	/**
	 * The actual message.
	 */
	private String message;

	public ChatMessageSentMessage() {

	}

	public ChatMessageSentMessage(short senderId, String message) {
		this.senderId = senderId;
		this.message = message;
	}

	public short getSenderId() {
		return senderId;
	}

	public String getMessage() {
		return message;
	}

}
