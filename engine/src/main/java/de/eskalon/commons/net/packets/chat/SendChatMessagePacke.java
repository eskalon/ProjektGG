package de.eskalon.commons.net.packets.chat;

public final class SendChatMessagePacke {

	private String message;

	public SendChatMessagePacke() {
		// default public constructor
	}

	public SendChatMessagePacke(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
