package dev.gg.network.message;

import java.util.List;

import dev.gg.command.PlayerCommand;

/**
 * This message contains all {@link #commands} by a player for a specific
 * {@link #turn} and is sent by the client to the server every turn.
 */
public class ClientTurnMessage {

	/**
	 * The turn in which these commands should get executed. Is automatically
	 * filled in by the game.
	 */
	private int turn;
	/**
	 * All commands for the specific turn. Null if no commands where issued.
	 */
	private List<PlayerCommand> commands;
	/**
	 * The ID of the player issuing the commands. Identical to
	 * {@link #commands#clientID}.
	 */
	private short clientID;

	public ClientTurnMessage() {

	}

	public ClientTurnMessage(List<PlayerCommand> commands, int turn,
			short clientID) {
		this.commands = commands;
		this.turn = turn;
		this.clientID = clientID;
	}

	public List<PlayerCommand> getCommands() {
		return commands;
	}

	public int getTurn() {
		return turn;
	}

	public short getClientID() {
		return clientID;
	}

}
