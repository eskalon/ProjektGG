package dev.gg.network.message;

import java.util.List;

import dev.gg.command.PlayerCommand;

/**
 * This class contains all command messages for a turn and is sent by the
 * server.
 */
public class TurnCommandsMessage {

	private List<PlayerCommand> commands;

	public TurnCommandsMessage() {

	}

	public TurnCommandsMessage(List<PlayerCommand> commands) {
		this.commands = commands;
	}

	public List<PlayerCommand> getCommands() {
		return commands;
	}

}
