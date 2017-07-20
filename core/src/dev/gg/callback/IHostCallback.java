package dev.gg.callback;

import java.io.IOException;

public interface IHostCallback {

	/**
	 * Called when the host's server and client started.
	 * 
	 * @param e
	 *            Not null if a problem occurred while starting either the
	 *            client or the server.
	 */
	public void onHostStarted(IOException e);

}
