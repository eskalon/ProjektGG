package de.eskalon.gg.events;

import java.util.HashMap;

import de.eskalon.gg.net.GameClient;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Is posted by the {@link GameClient} when a vote is over.
 */
@AllArgsConstructor
public class VoteFinishedEvent {

	private @Getter HashMap<Short, Integer> individualVotes;

}
