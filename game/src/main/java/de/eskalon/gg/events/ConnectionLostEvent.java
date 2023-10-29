package de.eskalon.gg.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Is posted when the client disconnects unexpectedly from the server.
 */
@AllArgsConstructor
public final class ConnectionLostEvent {

	private @Getter boolean disconnectedByChoice;

}
