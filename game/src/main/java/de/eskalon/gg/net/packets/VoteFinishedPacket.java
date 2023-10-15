package de.eskalon.gg.net.packets;

import java.util.HashMap;

public final class VoteFinishedPacket {

	private HashMap<Short, Integer> individualVotes;

	public VoteFinishedPacket() {
		// default public constructor
	}

	public VoteFinishedPacket(HashMap<Short, Integer> individualVotes) {
		this.individualVotes = individualVotes;
	}

	public HashMap<Short, Integer> getIndividualVotes() {
		return individualVotes;
	}

}
