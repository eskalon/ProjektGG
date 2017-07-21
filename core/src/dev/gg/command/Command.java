package dev.gg.command;

import dev.gg.core.GameSession;

/**
 * The abstract base class for all commands. Is supposed to only hold data, the
 * logic takes place in {@link GameSession#processCommand(PlayerCommands)}.
 */
public abstract class Command {

	public Command() {

	}

}
