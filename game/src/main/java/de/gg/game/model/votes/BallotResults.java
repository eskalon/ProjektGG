package de.gg.game.model.votes;

import java.util.HashMap;

/**
 * This class describes the results of a vote.
 */
public class BallotResults {

	/**
	 * A hashmap of the individually cast votes.
	 *
	 * @see BallotOption#getValue()
	 */
	private HashMap<Short, Integer> individualVotes;
	/**
	 * The {@linkplain BallotOption#getValue() value} of the overall result.
	 */
	private int overallResult;

	public BallotResults() {
		this.individualVotes = new HashMap<>();
	}

	public BallotResults(int overallResult,
			HashMap<Short, Integer> individualVotes) {
		this.overallResult = overallResult;
		this.individualVotes = individualVotes;
	}

	/**
	 * A hashmap containing each individual vote. The characters's ID is the key
	 * and the vote option is the value.
	 *
	 * @return the individual votes.
	 */
	public HashMap<Short, Integer> getIndividualVotes() {
		return individualVotes;
	}

	public int getOverallResult() {
		return overallResult;
	}

	public void setOverallResult(int overallResult) {
		this.overallResult = overallResult;
	}

}
