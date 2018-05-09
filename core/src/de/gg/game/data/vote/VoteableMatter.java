package de.gg.game.data.vote;

import java.util.List;

/**
 * This class represents a matter on which a vote is held on.
 */
public abstract class VoteableMatter {

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
	public abstract List<VoteOption> getOptions();

	/**
	 * @param voteResults
	 *            The results of the vote.
	 * @return the text displayed to describe the vote result.
	 */
	public abstract String getResultText(VoteResults voteResults);

}
