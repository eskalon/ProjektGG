package de.gg.engine.integration;

import java.time.OffsetDateTime;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;

import de.gg.engine.log.Log;

public abstract class DiscordRichPresenceHandler {

	protected boolean enabled = false;
	protected IPCClient client;

	protected DiscordRichPresenceHandler(long clientId) {
		this.client = new IPCClient(clientId);

		client.setListener(new IPCListener() {
			@Override
			public void onReady(IPCClient client) {
				setMenuPresence();
			}
		});
	}

	protected abstract void setMenuPresence();

	protected RichPresence.Builder createBasicBuilder(String state) {
		return (new RichPresence.Builder())
				.setStartTimestamp(OffsetDateTime.now());
	}

	public void connect() {
		(new Thread(() -> {
			try {
				client.connect();
				enabled = true;
			} catch (NoDiscordClientException e) {
				Log.error("Discord",
						"Couldn't connect to the discord servers: %s", e);
			}
		})).start();
	}

	public void disconnect() {
		enabled = false;
		client.close();
	}

}
