package dev.gg.network.message;

import dev.gg.command.PlayerCommands;

/**
 * This message contains all {@link #commands} by a player for a specific
 * {@link #turn} and is sent by the client to the server every turn.
 */
public class PlayerTurnMessage {

	/**
	 * The turn in which these commands should get executed. Is automatically
	 * filled in by the game.
	 */
	private int turn;
	/**
	 * All commands for the specific turn. Null if no commands where issued.
	 */
	private PlayerCommands commands;
	/**
	 * The ID of the player issuing the commands. Identical to
	 * {@link #commands#clientID}.
	 */
	private short clientID;

	public PlayerTurnMessage() {

	}

	public PlayerTurnMessage(PlayerCommands commands, int turn,
			short clientID) {
		this.commands = commands;
		this.turn = turn;
		this.clientID = clientID;
	}

	public PlayerCommands getCommands() {
		return commands;
	}

	public int getTurn() {
		return turn;
	}

	public short getClientID() {
		return clientID;
	}

}
