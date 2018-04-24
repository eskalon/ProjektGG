package de.gg.event;

import de.gg.game.data.GameSpeed;

/**
 * Is posted when the game speed changes.
 */
public class ChangedGameSpeedEvent {

	private GameSpeed gameSpeed;

	public ChangedGameSpeedEvent(GameSpeed gameSpeed) {
		this.gameSpeed = gameSpeed;
	}

}
