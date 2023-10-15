package de.eskalon.gg.events;

import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.net.packets.sync.LobbyDataChangedPacket.ChangeType;

public final class LobbyDataChangedEvent {

	private LobbyData oldData;
	private LobbyData newData;
	private ChangeType changeType;

	public LobbyDataChangedEvent(LobbyData oldData, LobbyData newData,
			ChangeType changeType) {
		this.oldData = oldData;
		this.newData = newData;
		this.changeType = changeType;
	}

	public LobbyData getOldData() {
		return oldData;
	}

	public LobbyData getNewData() {
		return newData;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

}
