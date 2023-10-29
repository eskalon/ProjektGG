package de.eskalon.commons.net.packets.chat;

public final class S2CChatMessageReceivedPacket {

	private short sender;
	private String message;

	public S2CChatMessageReceivedPacket() {
		// default public constructor
	}

	public S2CChatMessageReceivedPacket(short sender, String message) {
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
