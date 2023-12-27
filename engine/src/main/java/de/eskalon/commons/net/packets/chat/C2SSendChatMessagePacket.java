package de.eskalon.commons.net.packets.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public final class C2SSendChatMessagePacket {

	private @Getter String message;

}
