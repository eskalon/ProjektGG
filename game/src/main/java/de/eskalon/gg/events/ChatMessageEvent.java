package de.eskalon.gg.events;

import de.eskalon.commons.net.data.ChatMessage;

public final class ChatMessageEvent<P> {

	private ChatMessage<P> msg;

	public ChatMessageEvent(ChatMessage<P> msg) {
		this.msg = msg;
	}

	public ChatMessage<P> getMsg() {
		return msg;
	}

}
