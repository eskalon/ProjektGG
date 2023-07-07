package de.gg.game.ui.data;

import javax.annotation.Nullable;

import de.gg.game.network.PlayerData;

public final class ChatMessage {

	/**
	 * {@code null} if this is a system message
	 */
	private PlayerData player;
	private String message;

	public ChatMessage(String systemMessage) {
		this(null, systemMessage);
	}

	public ChatMessage(@Nullable PlayerData player, String message) {
		this.player = player;
		this.message = message;
	}

	public PlayerData getSender() {
		return player;
	}

	public String getMessage() {
		return message;
	}

	public boolean isSystemMessage() {
		return player == null;
	}

}
