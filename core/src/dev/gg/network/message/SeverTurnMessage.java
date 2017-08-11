package dev.gg.network.message;

import java.util.HashMap;
import java.util.List;

import dev.gg.command.PlayerCommand;

/**
 * This class contains all command messages for a specific {@link #turn} and is
 * sent by server to the client.
 */
public class SeverTurnMessage {

	private int turn;
	private HashMap<Short, List<PlayerCommand>> commands;

	public SeverTurnMessage() {

	}

	public SeverTurnMessage(HashMap<Short, List<PlayerCommand>> commands,
			int turn) {
		this.commands = commands;
		this.turn = turn;
	}

	public int getTurn() {
		return turn;
	}

	public HashMap<Short, List<PlayerCommand>> getPlayerCommands() {
		return commands;
	}

}
