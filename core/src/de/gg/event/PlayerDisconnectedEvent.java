package de.gg.event;

/**
 * Called when a player disconnects.
 */
public class PlayerDisconnectedEvent {

	private short playerId;

	public PlayerDisconnectedEvent(short playerId) {
		this.playerId = playerId;
	}

	public short getId() {
		return playerId;
	}

}
