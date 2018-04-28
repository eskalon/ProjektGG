package de.gg.event;

import de.gg.game.data.RoundEndData;

/**
 * Posted after a the client received the round end data by the server.
 */
public class RoundEndDataReceivedEvent {

	private RoundEndData data;

	public RoundEndDataReceivedEvent(RoundEndData data) {
		this.data = data;
	}

	public RoundEndData getData() {
		return data;
	}

}
