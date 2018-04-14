package de.gg.event;

import de.gg.data.RoundEndData;

/**
 * Posted after a the client received the round end data.
 */
public class RoundEndEvent {

	private RoundEndData data;

	public RoundEndEvent(RoundEndData data) {
		this.data = data;
	}

	public RoundEndData getData() {
		return data;
	}

}
