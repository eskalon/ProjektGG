package de.eskalon.gg.events;

import de.eskalon.commons.net.data.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class ChatMessageEvent<P> {

	private @Getter ChatMessage<P> msg;

}
