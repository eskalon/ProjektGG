package dev.gg.callback;

import java.io.IOException;

public interface IClientCallback {

	/**
	 * Called when the client connected to the server.
	 * 
	 * @param e
	 *            Not null if a problem occurred while starting the client.
	 */
	public void onClientConnected(IOException e);

}
