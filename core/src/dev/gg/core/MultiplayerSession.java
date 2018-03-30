package dev.gg.core;

import java.util.HashMap;

import dev.gg.data.GameSessionSetup;
import dev.gg.data.RoundEndData;
import dev.gg.util.StoppableRunnable;

/**
 * This class simulates a game session on the client of a multiplayer game. It
 * is also implementing the interface used for the RMI by the server.
 */
public class MultiplayerSession extends GameSession {

	/**
	 * The network ID of the local player.
	 */
	private short localId;
	private StoppableRunnable updateThread;

	/**
	 * Creates a new multiplayer session.
	 * 
	 * @param sessionSetup
	 *            The settings of the game session.
	 * @param players
	 *            A hashmap containing the players.
	 * @param networkID
	 *            The networkID of the local player.
	 */
	public MultiplayerSession(GameSessionSetup sessionSetup,
			HashMap<Short, LobbyPlayer> players, short networkID) {
		super(sessionSetup);
		this.localId = networkID;

		// TODO Die restlichen Spieler über die players-Liste in #city
		// aufsetzen

	}

	/**
	 * Starts the thread that updates the game logic. After a round is over the
	 * game automatically switches to the round end screen. To resume the game
	 * {@link #setupNewRound(RoundEndData)} has to get called.
	 * 
	 * @param game
	 *            The game.
	 */
	public void startGame(ProjektGG game) {
		this.updateThread = new StoppableRunnable() {
			@Override
			public void doStuff() {
				if (update()) {
					game.pushScreen("roundEnd");
				}
			}
		};
	}

	public void stopGame() {
		updateThread.stop();
	}

}
