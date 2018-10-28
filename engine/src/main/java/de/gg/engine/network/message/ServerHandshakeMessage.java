package de.gg.engine.network.message;

public class ServerHandshakeMessage {

	/**
	 * The hostname of the client's machine.
	 */
	private String msg;
	private boolean successful;
	private short clientNetworkId;

	public ServerHandshakeMessage() {
		// default public constructor
	}

	public ServerHandshakeMessage(short clientNetworkId) {
		this.msg = null;
		this.successful = true;
		this.clientNetworkId = clientNetworkId;
	}

	public ServerHandshakeMessage(String msg) {
		this.msg = msg;
		this.successful = false;
		this.clientNetworkId = -1;
	}

	public String getMsg() {
		return msg;
	}

	public boolean wasSuccessful() {
		return successful;
	}

	/**
	 * @return the client's network id. <code>-1</code> if the handshake was
	 *         unsuccessful.
	 */
	public short getClientNetworkId() {
		return clientNetworkId;
	}

}
