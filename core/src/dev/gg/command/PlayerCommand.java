package dev.gg.command;

import dev.gg.core.GameSession;

/**
 * The abstract base class for all commands. Is supposed to only hold data, the
 * logic takes place in {@link GameSession#processCommand(BaseCommandMessage)}.
 */
public abstract class PlayerCommand {

	/**
	 * The turn in which this command should get executed. Is automatically
	 * filled in by the game.
	 */
	private int turn;
	/**
	 * The ID of the player executing this command. Is automatically filled in
	 * by the game.
	 */
	private int senderID;

	public PlayerCommand() {

	}

	public void setSenderID(int senderID) {
		this.senderID = senderID;
	}

	public int getSenderID() {
		return senderID;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}
	public int getTurn() {
		return turn;
	}

}
