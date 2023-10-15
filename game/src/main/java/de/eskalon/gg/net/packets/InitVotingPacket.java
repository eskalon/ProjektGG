package de.eskalon.gg.net.packets;

import de.eskalon.gg.simulation.model.votes.Ballot;

public final class InitVotingPacket {

	private Ballot matterToVoteOn;

	public InitVotingPacket() {
		// default public constructor
	}

	public InitVotingPacket(Ballot matterToVoteOn) {
		this.matterToVoteOn = matterToVoteOn;
	}

	public Ballot getMatterToVoteOn() {
		return matterToVoteOn;
	}

}
