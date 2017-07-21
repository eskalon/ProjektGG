package dev.gg.command;

import java.util.List;

/**
 * This class represents all player commands for one turn.
 */
public class PlayerCommands {

	/**
	 * The commands issued by the player.
	 */
	private List<Command> commands;
	/**
	 * The ID of the player issuing the commands. Only needed in the
	 * multiplayer.
	 */
	private short playerID;

	public PlayerCommands() {
	}

	public PlayerCommands(List<Command> commands, short playerID) {
		this.commands = commands;
		this.playerID = playerID;
	}

	public short getPlayerID() {
		return playerID;
	}

	public List<Command> getCommands() {
		return commands;
	}

}
