package dev.gg.network.message;

/**
 * This message is sent after a chat message got written.
 */
public class ChatMessageSentMessage {

	/**
	 * The sending player's ID.
	 */
	private int senderId;
	/**
	 * The actual message.
	 */
	private String message;

	public ChatMessageSentMessage() {

	}

	public ChatMessageSentMessage(int senderId, String message) {
		this.senderId = senderId;
		this.message = message;
	}

	public int getSenderId() {
		return senderId;
	}

	public String getMessage() {
		return message;
	}

}
