package de.eskalon.gg.simulation.model.votes;

import java.util.HashMap;
import java.util.List;

import de.eskalon.gg.simulation.model.World;

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
	 * @param result
	 *            the result
	 * @return the text displayed to describe the vote's result
	 */
	public abstract String getResultText(int result);

	/**
	 * Processes the results of a vote.
	 *
	 * @param individualVotes
	 *            the cast votes
	 * @param result
	 *            the result
	 * @param world
	 *            the world this vote took place in
	 */
	public abstract void processVoteResult(
			HashMap<Short, Integer> individualVotes, int result, World world);

}
