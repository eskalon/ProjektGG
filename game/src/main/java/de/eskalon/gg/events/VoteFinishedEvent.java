package de.eskalon.gg.events;

import java.util.HashMap;

import de.eskalon.gg.net.GameClient;

/**
 * Is posted by the {@link GameClient} when a vote is over.
 */
public class VoteFinishedEvent {

	private HashMap<Short, Integer> individualVotes;

	public VoteFinishedEvent(HashMap<Short, Integer> individualVotes) {
		this.individualVotes = individualVotes;
	}

	public HashMap<Short, Integer> getIndividualVotes() {
		return individualVotes;
	}

}
