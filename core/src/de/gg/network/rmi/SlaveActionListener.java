package de.gg.network.rmi;

/**
 * This interface takes care of a player's actions. Those are mainly triggered
 * via the ui and then relayed to the server via this interface. The
 * implementing server-side class is responsible for informing the server of
 * their planned action. The server returns a boolean, <code>true</code>
 * indicating an successful action (-> the UI effect gets triggered, but the
 * actual game does not (!) change), <code>false</code> indicating the need to
 * reload the ui (because a value seems to be out of synch and should be synched
 * by now). Actual changes to the game do not happen when a method of this class
 * is called, but wait for the server's call to the local
 * {@link AuthoritativeResultListener}.
 */
public interface SlaveActionListener {

	public boolean readyUp(short networkId);

	public void increaseGameSpeed(short clientId);

	public void decreaseGameSpeed(short clientId);

}
