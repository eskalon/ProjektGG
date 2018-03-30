package de.gg.event;

import java.io.IOException;

/**
 * Is posted when the client is connected to the server.
 */
public class ConnectionEstablishedEvent {

	/**
	 * <i>Not<i> null if a problem occurred while starting the client.
	 */
	private IOException e;

	public ConnectionEstablishedEvent() {
	}

	public ConnectionEstablishedEvent(IOException e) {
		this.e = e;
	}

	public IOException getException() {
		return e;
	}

}
