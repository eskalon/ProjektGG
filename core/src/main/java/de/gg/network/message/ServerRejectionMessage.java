package de.gg.network.message;

/**
 * A child class of this message is sent if the server rejected the connection.
 */
public abstract class ServerRejectionMessage {

	public abstract String getMessage();

}
