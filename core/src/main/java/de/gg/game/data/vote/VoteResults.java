package de.gg.game.data.vote;

import java.util.HashMap;

/**
 * This class describes the results of a vote.
 */
public class VoteResults {

	/**
	 * A hashmap of the individual votes.
	 *
	 * @see VoteOption#getValue()
	 */
	private HashMap<Short, Integer> individualVotes;
	/**
	 * The {@linkplain VoteOption#getValue() value} of the overall result.
	 */
	private int overallResult;

	public VoteResults() {
		this.individualVotes = new HashMap<>();
	}

	public VoteResults(int overallResult,
			HashMap<Short, Integer> individualVotes) {
		this.overallResult = overallResult;
		this.individualVotes = individualVotes;
	}

	public HashMap<Short, Integer> getIndividualVotes() {
		return individualVotes;
	}

	public void setIndividualVotes(HashMap<Short, Integer> individualVotes) {
		this.individualVotes = individualVotes;
	}

	public int getOverallResult() {
		return overallResult;
	}

	public void setOverallResult(int overallResult) {
		this.overallResult = overallResult;
	}

}
