package de.gg.game.events;

import de.gg.game.types.GameSpeed;

/**
 * Is posted when the game speed changes.
 */
public class ChangedGameSpeedEvent {

	private GameSpeed gameSpeed;

	public ChangedGameSpeedEvent(GameSpeed gameSpeed) {
		this.gameSpeed = gameSpeed;
	}

	public GameSpeed getGameSpeed() {
		return gameSpeed;
	}

}
