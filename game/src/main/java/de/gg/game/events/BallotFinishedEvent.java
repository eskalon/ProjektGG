package de.gg.game.events;

import de.gg.game.model.votes.Ballot;
import de.gg.game.model.votes.BallotResults;
import de.gg.game.network.GameClient;

/**
 * Is posted by the {@link GameClient} when a ballot is over.
 */
public class BallotFinishedEvent {

	private BallotResults results;
	private Ballot ballot;

	public BallotFinishedEvent(BallotResults results, Ballot ballot) {
		this.results = results;
		this.ballot = ballot;
	}

	public BallotResults getResults() {
		return results;
	}

	public Ballot getBallot() {
		return ballot;
	}

}
