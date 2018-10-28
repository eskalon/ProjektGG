package de.gg.game.events;

import de.gg.game.data.vote.VoteResults;
import de.gg.game.network.GameClient;
import de.gg.game.votes.VoteableMatter;

/**
 * Is posted by the {@link GameClient} when a vote is over.
 */
public class VoteFinishedEvent {

	private VoteResults results;
	private VoteableMatter matterToVoteOn;

	public VoteFinishedEvent(VoteResults results,
			VoteableMatter matterToVoteOn) {
		this.results = results;
		this.matterToVoteOn = matterToVoteOn;
	}

	public VoteResults getResults() {
		return results;
	}

	public VoteableMatter getMatterToVoteOn() {
		return matterToVoteOn;
	}

}
