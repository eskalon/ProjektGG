package dev.gg.network.message;

import dev.gg.command.PlayerCommand;

/**
 * This class is used to wrap the commands sent by the clients.
 */
public class NewCommandMessage {

	/**
	 * The command issued by the player.
	 */
	private PlayerCommand command;

	public NewCommandMessage() {

	}

	public NewCommandMessage(PlayerCommand command) {
		this.command = command;
	}

	public PlayerCommand getCommand() {
		return command;
	}

}
