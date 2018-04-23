package de.gg.event;

import java.io.IOException;

import de.gg.network.message.ServerRejectionMessage;

/**
 * Is posted when the client connection process failed.
 */
public class ConnectionFailedEvent {

	/**
	 * The exception that was thrown while starting the client.
	 * <code>Null</code> if {@link #serverRejectMessage} is set.
	 */
	private IOException exception = null;
	/**
	 * The rejection message sent by the server. <code>Null</code> if
	 * {@link #exception} is set.
	 */
	private ServerRejectionMessage serverRejectionMessage;

	public ConnectionFailedEvent(IOException exception) {
		this.exception = exception;
	}

	public ConnectionFailedEvent(
			ServerRejectionMessage serverRejectionMessage) {
		this.serverRejectionMessage = serverRejectionMessage;
	}

	public ServerRejectionMessage getServerRejectionMessage() {
		return serverRejectionMessage;
	}

	public IOException getException() {
		return exception;
	}

}
