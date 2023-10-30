package de.eskalon.commons.net.packets.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public final class S2CChatMessageReceivedPacket {

	private @Getter short sender;
	private @Getter String message;

}
