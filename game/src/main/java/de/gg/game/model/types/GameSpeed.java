package de.gg.game.model.types;

/**
 * Describes the speed of the game session.
 */
public enum GameSpeed {

	SLOW(1), NORMAL(2), SPEED_2(4), SPEED_3(8), SPEED_4(16);

	/**
	 * This value is multiplied with the delta time to simulate the game speed.
	 */
	private int deltaTimeMultiplied;

	GameSpeed(int deltaTimeMultiplied) {
		this.deltaTimeMultiplied = deltaTimeMultiplied;
	}

	public int getDeltaTimeMultiplied() {
		return deltaTimeMultiplied;
	}

}
