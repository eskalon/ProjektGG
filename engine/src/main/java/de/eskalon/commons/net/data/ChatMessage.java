package de.eskalon.commons.net.data;

import org.jspecify.annotations.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class ChatMessage<P> {

	/**
	 * {@code null} if this is a system message
	 */
	private @Getter @Nullable P sender;
	private @Getter String message;

	public ChatMessage(String systemMessage) {
		this(null, systemMessage);
	}

	public boolean isSystemMessage() {
		return sender == null;
	}

}
