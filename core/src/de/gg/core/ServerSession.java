package de.gg.core;

import java.util.HashMap;

import de.gg.data.GameSessionSetup;
import de.gg.data.RoundEndData;
import de.gg.util.StoppableRunnable;

/**
 * This class takes care of simulating the game session on the server side and
 * implements the interface used in the RMI for the client.
 */
public class ServerSession extends GameSession {

	/**
	 * The network ID of the local player.
	 */
	private short localId;
	private StoppableRunnable updateThread;
	/**
	 * Set to true when a game round is over. The next round should start, when
	 * all players issued a ready message.
	 * 
	 * @see GameSession#setupNewRound(RoundEndData) Method to start the next
	 *      round
	 */
	private boolean waitingForNextRound = false;

	/**
	 * Creates a new multiplayer session.
	 * 
	 * @param sessionSetup
	 *            The settings of the game session.
	 * @param players
	 *            A hashmap containing the players.
	 */
	public ServerSession(GameSessionSetup sessionSetup,
			HashMap<Short, LobbyPlayer> players) {
		super(sessionSetup);

		// TODO Alle Spieler über die players-Liste in #city
		// aufsetzen

	}

	/**
	 * Starts the thread that updates the game logic. After a round is over the
	 * game automatically switches to the round end screen. To resume the game
	 * {@link #setupNewRound(RoundEndData)} has to get called.
	 */
	public void startGame() {
		this.updateThread = new StoppableRunnable() {
			@Override
			public void doStuff() {
				if (update()) {
					waitingForNextRound = true;
				}
			}
		};
	}

	public void stopGame() {
		updateThread.stop();

		// TODO save the game
	}

}
