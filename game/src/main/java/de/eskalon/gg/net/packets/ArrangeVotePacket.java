package de.eskalon.gg.net.packets;

import de.eskalon.gg.net.packets.data.VoteType;

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

}
