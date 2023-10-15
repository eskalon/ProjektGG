package de.eskalon.gg.net.packets;

public final class CastVotePacket {

	private int option;

	public CastVotePacket() {
		// default public constructor
	}

	public CastVotePacket(int option) {
		this.option = option;
	}

	public int getOption() {
		return option;
	}

}
