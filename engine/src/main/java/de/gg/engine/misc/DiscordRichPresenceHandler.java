package de.gg.engine.misc;

import java.time.OffsetDateTime;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;

import de.damios.guacamole.ISimpleCallback;
import de.damios.guacamole.Preconditions;
import de.damios.guacamole.gdx.Log;
import de.eskalon.commons.misc.ThreadHandler;

public abstract class DiscordRichPresenceHandler {

	/**
	 * Whether the handler is enabled.
	 */
	protected boolean enabled = false;
	/**
	 * Whether the handler is currently connected to the local discord IPC. If
	 * it is not, it is trying to connect every time the presence is changed.
	 */
	protected boolean connected = false;
	private IPCClient client;

	protected DiscordRichPresenceHandler(long clientId) {
		this.client = new IPCClient(clientId);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	protected RichPresence.Builder createBasicBuilder(String details) {
		return (new RichPresence.Builder()).setDetails(details)
				.setStartTimestamp(OffsetDateTime.now());
	}

	protected void setPresence(RichPresence presence) {
		if (!enabled)
			return;

		if (connected)
			client.sendRichPresence(presence);
		else
			connect((Object) -> {
				client.sendRichPresence(presence);
			});
	}

	protected void connect(ISimpleCallback callback) {
		if (!enabled)
			return;

		Preconditions.checkState(!connected,
				"Can't connect twice to the discord ipc");

		ThreadHandler.getInstance().executeRunnable(() -> {
			try {
				client.setListener(new IPCListener() {
					@Override
					public void onReady(IPCClient client) {
						connected = true;
						callback.call(null);
						Log.info("Discord",
								"Connection to the ipc established");
					}
				});
				client.connect();
			} catch (NoDiscordClientException e) {
				Log.error("Discord", "Couldn't connect to the ipc: %s", e);
			}
		});
	}

	public void disconnect() {
		if (!enabled)
			return;

		connected = false;
		client.close();
	}

}
