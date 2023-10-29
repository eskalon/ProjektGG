package de.eskalon.gg.events;

/**
 * Is posted when the client disconnects unexpectedly from the server.
 */
public final class ConnectionLostEvent {

	private boolean disconnectedByChoice;

	public ConnectionLostEvent(boolean disconnectedByChoice) {
		this.disconnectedByChoice = disconnectedByChoice;
	}

	public boolean isDisconnectedByChoice() {
		return disconnectedByChoice;
	}

}
