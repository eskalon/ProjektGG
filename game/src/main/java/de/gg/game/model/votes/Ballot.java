package de.gg.game.model.votes;

import java.util.List;

import de.gg.game.model.World;

/**
 * This class represents a matter on which a vote is held on.
 */
public abstract class Ballot {

	/**
	 * @return the info text displayed at the beginning of the vote.
	 */
	public abstract String getInfoText();

	/**
	 * @return a list of the character ids of all eligible voters.
	 */
	public abstract List<Short> getVoters();

	/**
	 * @return a list of all options that can be chosen.
	 */
	public abstract List<BallotOption> getOptions();

	/**
	 * @param voteResults
	 *            The results of the vote.
	 * @return the text displayed to describe the vote's result.
	 */
	public abstract String getResultText(BallotResults voteResults);

	/**
	 * Processes the results of a vote.
	 *
	 * @param result
	 *            The result of the vote.
	 * @param world
	 *            The world this vote took place in.
	 */
	public abstract void processVoteResult(BallotResults result, World world);

}
