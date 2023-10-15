package de.eskalon.commons.net.packets.chat;

public final class ChatMessageReceivedPacket {

	private short sender;
	private String message;

	public ChatMessageReceivedPacket() {
		// default public constructor
	}

	public ChatMessageReceivedPacket(short sender, String message) {
		this.sender = sender;
		this.message = message;
	}

	public short getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}

}
