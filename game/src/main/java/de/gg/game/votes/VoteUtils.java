package de.gg.game.votes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import de.gg.engine.utils.CollectionUtils;

public class VoteUtils {

	private VoteUtils() {
		// not used
	}

	/**
	 *
	 * @param matterToVoteOn
	 * @param individualVotes
	 * @param seed
	 * @return the {@linkplain VoteOption#getValue() value} of the overall
	 *         result of the matter.
	 */
	public static int getVoteResult(VoteableMatter matterToVoteOn,
			HashMap<Short, Integer> individualVotes, long seed) { // Find the
																	// most
																	// common
		// vote option
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
			if (!(matterToVoteOn instanceof ElectionVote)
					&& matterToVoteOn.getOptions().size() == 2) // the majority
																// is needed
				return -1;
			else // Random option wins
				return CollectionUtils.getRandomElementInList(resultOptions,
						new Random(seed));

		} else {
			// One result
			return entries[0].getKey();
		}
	}

}
