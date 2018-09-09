package de.gg.events;

/**
 * Posted when all players readied up and the screen should change.
 */
public class AllPlayersReadyEvent {

	private boolean nextRound;

	public AllPlayersReadyEvent(boolean nextRound) {
		this.nextRound = nextRound;
	}

	/**
	 * @return whether this event also marks the beginning of a new round.
	 */
	public boolean isNextRound() {
		return nextRound;
	}

}
