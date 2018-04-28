package de.gg.network.rmi;

/**
 * This class is an convenience wrapper for {@link SlaveActionListener}. It is
 * used to relay the player actions to the server.
 */
public class ClientActionHandler {

	private short networkId;
	private SlaveActionListener actionListener;

	public ClientActionHandler(short networkId,
			SlaveActionListener actionListener) {
		this.networkId = networkId;
		this.actionListener = actionListener;
	}

	public boolean readyUp() {
		return actionListener.readyUp(networkId);
	}

	public void increaseGameSpeed() {
		actionListener.increaseGameSpeed(networkId);
	}

	public void decreaseGameSpeed() {
		actionListener.decreaseGameSpeed(networkId);
	}

}
