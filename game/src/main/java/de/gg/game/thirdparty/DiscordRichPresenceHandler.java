package de.gg.game.thirdparty;

import java.time.OffsetDateTime;
import java.util.function.Consumer;

import org.lwjgl.system.Callback;
import org.lwjgl.system.CallbackI;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;

import de.damios.guacamole.Preconditions;
import de.damios.guacamole.concurrent.ThreadHandler;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;

public abstract class DiscordRichPresenceHandler {

	private static final Logger LOG = LoggerService
			.getLogger(DiscordRichPresenceHandler.class);
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
			connect(() -> {
				client.sendRichPresence(presence);
			});
	}

	protected void connect(Runnable onCompletion) {
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
						onCompletion.run();
						LOG.info("[DISCORD] Connection to the ipc established");
					}
				});
				client.connect();
			} catch (NoDiscordClientException e) {
				LOG.error("[DISCORD] Couldn't connect to the ipc: %s", e);
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
