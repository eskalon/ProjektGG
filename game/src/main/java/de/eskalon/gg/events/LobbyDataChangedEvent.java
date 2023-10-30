package de.eskalon.gg.events;

import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.net.packets.sync.S2CLobbyDataChangedPacket.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class LobbyDataChangedEvent {

	private @Getter LobbyData oldData;
	private @Getter LobbyData newData;
	private @Getter ChangeType changeType;

}
