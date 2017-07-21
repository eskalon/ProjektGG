package dev.gg.network.message;

import java.util.List;

import dev.gg.command.PlayerCommands;

/**
 * This class contains all command messages for a specific {@link #turn} and is
 * sent by server to the client.
 */
public class TurnCommandsMessage {

	private int turn;
	private List<PlayerCommands> commandMessages;

	public TurnCommandsMessage() {

	}

	public TurnCommandsMessage(List<PlayerCommands> commandMessages, int turn) {
		this.commandMessages = commandMessages;
		this.turn = turn;
	}

	public int getTurn() {
		return turn;
	}

	public List<PlayerCommands> getPlayerCommands() {
		return commandMessages;
	}

}
