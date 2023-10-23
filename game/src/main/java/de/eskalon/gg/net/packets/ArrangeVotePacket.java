package de.eskalon.gg.net.packets;

import de.eskalon.gg.net.packets.data.VoteType;
import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.votes.Ballot;
import de.eskalon.gg.simulation.model.votes.ImpeachmentBallot;

public final class ArrangeVotePacket {

	private VoteType type;
	private short caller;
	private short target;

	public ArrangeVotePacket() {
		// default public constructor
	}

	public ArrangeVotePacket(VoteType type, short caller, short target) {
		this.type = type;
		this.caller = caller;
		this.target = target;
	}

	public VoteType getType() {
		return type;
	}

	public short getCaller() {
		return caller;
	}

	public short getTarget() {
		return target;
	}

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
