package de.eskalon.gg.net.packets;

import de.eskalon.gg.net.packets.data.VoteType;
import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.votes.Ballot;
import de.eskalon.gg.simulation.model.votes.ImpeachmentBallot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public final class ArrangeVotePacket {

	private @Getter VoteType type;
	private @Getter short caller;
	private @Getter short target;

	public static Ballot createBallot(ArrangeVotePacket msg, World world) {
		switch (msg.getType()) {
		case IMPEACHMENT:
			return new ImpeachmentBallot(world,
					world.getCharacters().get(msg.getTarget()).getPosition(),
					msg.getCaller());
		case ELECTION:
			// return new ElectionBallot(world,
			// CollectionUtils.getKeyByValue(world.getPositions(), p)
		}
		return null;
	}

}
