package de.gg.screen;

import com.google.common.eventbus.Subscribe;

import de.gg.event.NewChatMessagEvent;
import de.gg.event.PlayerDisconnectedEvent;

public abstract class BaseGameScreen extends BaseScreen {

	// TODO dieser Screen bekommt eine Player-Hashmap, um sich um alle
	// Join/Leave und Chat-Events kümmern zu können (d.h. er kann für die
	// Netzwerk-IDs den jeweiligen Namen ermitteln)

	@Subscribe
	public void onNewChatMessage(NewChatMessagEvent event) {
		// TODO
	}

	@Subscribe
	public void onPlayerDisconnect(PlayerDisconnectedEvent event) {
		// TODO
	}

	protected void updateGame() {
		if (game.getCurrentSession().update()) {
			game.pushScreen("roundEnd");
		}

		game.getNetworkHandler().updateServer();
	}

}
