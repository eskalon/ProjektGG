package de.gg.game;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;

import de.gg.core.ProjektGG;
import de.gg.data.GameSessionSetup;
import de.gg.data.RoundEndData;
import de.gg.network.LobbyPlayer;
import de.gg.util.Log;
import de.gg.util.StoppableRunnable;

/**
 * This class simulates a game session on the client of a multiplayer game. It
 * is also implementing the interface used for the RMI by the server.
 */
public class SlaveSession extends GameSession
		implements
			AuthoritativeResultListener {

	/**
	 * The network ID of the local player.
	 */
	private short localId;
	private StoppableRunnable updateThread;
	private ProjektGG game;

	/**
	 * Creates a new multiplayer session.
	 * 
	 * @param game
	 *            An instance of the game.
	 * @param sessionSetup
	 *            The settings of the game session.
	 * @param players
	 *            A hashmap containing the players.
	 * @param networkID
	 *            The networkID of the local player.
	 */
	public SlaveSession(ProjektGG game, GameSessionSetup sessionSetup,
			HashMap<Short, LobbyPlayer> players, short networkID) {
		super(sessionSetup);
		this.localId = networkID;
		this.game = game;

		// TODO Die restlichen Spieler über die players-Liste in #city
		// aufsetzen

	}

	/**
	 * Starts the thread that updates the game logic. After a round is over the
	 * game automatically switches to the round end screen. To resume the game
	 * {@link #setupNewRound(RoundEndData)} has to get called.
	 */
	public void startGame() {
		this.updateThread = new GameSessionUpdateRunnable() {
			@Override
			protected void onRoundEnd() {
				if (getCurrentRound() > 0)
					game.pushScreen("roundEnd");
			}
		};
		(new Thread(this.updateThread)).start();
	}

	public void stopGame() {
		updateThread.stop();
	}

	@Override
	public synchronized void onAllPlayersReadied(RoundEndData data) {
		// TODO apply round end data

		Log.debug("Client", "Alle Spieler sind bereit! Nächste Runde startet");

		this.startNextRound();
		game.pushScreen("map");
	}

}
