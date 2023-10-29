package de.eskalon.commons.net.packets.chat;

public final class C2SSendChatMessagePacke {

	private String message;

	public C2SSendChatMessagePacke() {
		// default public constructor
	}

	public C2SSendChatMessagePacke(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
