package de.eskalon.commons.net.packets.lockstep;

import java.util.List;

import de.eskalon.commons.net.packets.data.IPlayerAction;

public final class C2SSendPlayerActionsPacket {

	private List<IPlayerAction> commands;
	private int turn;

	public C2SSendPlayerActionsPacket() {
		// default public constructor
	}

	public C2SSendPlayerActionsPacket(int turn, List<IPlayerAction> commands) {
		this.turn = turn;
		this.commands = commands;
	}

	public int getTurn() {
		return turn;
	}

	public List<IPlayerAction> getCommands() {
		return commands;
	}

}
