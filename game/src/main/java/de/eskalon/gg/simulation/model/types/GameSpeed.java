package de.eskalon.gg.simulation.model.types;

/**
 * Describes the speed of the game session.
 */
public enum GameSpeed {

	DEBUG(1000), VERY_SLOW(500), SLOW(200), NORMAL(100), FAST(60), FASTER(40);

	private int tickDuration;

	GameSpeed(int tickDuration) {
		this.tickDuration = tickDuration;
	}

	public int getTickDuration() {
		return tickDuration;
	}

}
