package dev.gg.callback;

import java.io.IOException;

public interface IClientCallback {

	/**
	 * Called when the client started.
	 * 
	 * @param e
	 *            Not null if a problem occurred while starting the client.
	 */
	public void onClientStarted(IOException e);

}
