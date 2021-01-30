package de.gg.game.events;

import javax.annotation.Nullable;

import de.gg.game.model.votes.Ballot;
import de.gg.game.session.SlaveSession;

/**
 * Is posted by the {@link SlaveSession} when a new ballot is started.
 */
public class NewBallotEvent {

	private @Nullable Ballot newBallot;

	public NewBallotEvent(@Nullable Ballot newBallot) {
		this.newBallot = newBallot;
	}

	/**
	 * @return the new ballot; is {@code null} if the voting process is over for
	 *         this round
	 */
	public @Nullable Ballot getNewBallot() {
		return newBallot;
	}

}
