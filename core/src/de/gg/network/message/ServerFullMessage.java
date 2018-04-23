package de.gg.network.message;

/**
 * This message is sent when the server is full.
 */
public class ServerFullMessage extends ServerRejectionMessage {

	public ServerFullMessage() {
	}

	@Override
	public String getMessage() {
		return "Der Server ist bereits voll";
	}

}
