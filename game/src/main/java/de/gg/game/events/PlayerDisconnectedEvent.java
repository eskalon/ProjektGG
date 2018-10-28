package de.gg.game.events;

/**
 * Posted when a player disconnects.
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
