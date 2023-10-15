package de.eskalon.commons.net;

import javax.annotation.Nullable;

public final class ChatMessage<P> {

	/**
	 * {@code null} if this is a system message
	 */
	private P player;
	private String message;

	public ChatMessage(String systemMessage) {
		this(null, systemMessage);
	}

	public ChatMessage(@Nullable P player, String message) {
		this.player = player;
		this.message = message;
	}

	public P getSender() {
		return player;
	}

	public String getMessage() {
		return message;
	}

	public boolean isSystemMessage() {
		return player == null;
	}

}
