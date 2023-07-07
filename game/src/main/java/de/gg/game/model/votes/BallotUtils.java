package de.gg.game.model.votes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import de.eskalon.commons.utils.RandomUtils;
import de.gg.game.misc.CollectionUtils;

public class BallotUtils {

	private BallotUtils() {
		// not used
	}

	/**
	 *
	 * @param ballot
	 * @param individualVotes
	 * @param seed
	 * @return the {@linkplain BallotOption#getValue() value} of the overall
	 *         result of the matter.
	 */
	public static int getBallotResult(Ballot ballot,
			HashMap<Short, Integer> individualVotes, long seed) { // Find the
																	// most
																	// common
		// Count votes
		Map<Integer, Integer> resultMap = new HashMap<>();
		for (int i : individualVotes.values()) {
			Integer count = resultMap.get(i);
			resultMap.put(i, count != null ? count + 1 : 1);
		}

		resultMap = CollectionUtils.sortByValue(resultMap);

		@SuppressWarnings("unchecked")
		Entry<Integer, Integer>[] entries = resultMap.entrySet()
				.toArray(new Entry[0]);
		// A tie
		if (entries.length > 1
				&& entries[0].getValue() == entries[1].getValue()) {
			List<Integer> resultOptions = new ArrayList<>();

			// Collect all tied options
			for (Entry<Integer, Integer> entry : entries) {
				if (entry.getValue() == entries[0].getValue()) {
					resultOptions.add(entry.getKey());
				}
			}
			if (!(ballot instanceof ElectionBallot)
					&& ballot.getOptions().size() == 2) // the majority
														// is needed
				return -1;
			else // Random option wins
				return RandomUtils.getElement(new Random(seed), resultOptions);
		} else {
			// One result
			return entries[0].getKey();
		}
	}

}
