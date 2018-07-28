package de.gg.event;

import de.gg.game.SlaveSession;
import de.gg.game.data.vote.VoteableMatter;

/**
 * Is posted by the {@link SlaveSession} when a new vote is started.
 */
public class NewVoteEvent {

	private VoteableMatter matterToVoteOn;

	public NewVoteEvent(VoteableMatter matterToVoteOn) {
		this.matterToVoteOn = matterToVoteOn;
	}

	/**
	 * @return the new matter to hold a vote on. Is <code>null</code> if the
	 *         voting process is over.
	 */
	public VoteableMatter getMatterToVoteOn() {
		return matterToVoteOn;
	}

}
