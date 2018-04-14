package de.gg.game;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import de.gg.data.GameSessionSetup;
import de.gg.data.RoundEndData;
import de.gg.network.LobbyPlayer;
import de.gg.util.Log;
import de.gg.util.PlayerUtils;
import de.gg.util.RandomUtils;

/**
 * This class takes care of simulating the game session on the server side and
 * implements the {@linkplain SlaveActionHandler interface} used in the RMI for
 * the client.
 */
public class AuthoritativeSession extends GameSession
		implements
			SlaveActionListener {

	private HashMap<Short, AuthoritativeResultListener> resultListeners;
	private ThreadPoolExecutor executor;

	/**
	 * Creates a new multiplayer session.
	 * 
	 * @param sessionSetup
	 *            The settings of the game session.
	 * @param players
	 *            A hashmap containing the players.
	 */
	public AuthoritativeSession(GameSessionSetup sessionSetup,
			HashMap<Short, LobbyPlayer> players, short localNetworkId) {
		super(sessionSetup, players, localNetworkId);

		this.executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
	}

	/**
	 * Starts the game session. {@link #update()} has to get called to update
	 * the session. To resume the game after a round ended
	 * {@link #setupNewRound(RoundEndData)} has to get called.
	 */
	public void startGame(
			HashMap<Short, AuthoritativeResultListener> resultListeners) {
		for (LobbyPlayer player : players.values()) {
			player.setReady(false);
		}

		this.resultListeners = resultListeners;

		super.setupGame();
	}

	public void stopGame() {
		// TODO save the game
	}

	@Override
	public void fixedUpdate() {
		super.fixedUpdate();

		if (isRightTick(15)) {
			// Update production progress
		}
	}

	public void onRoundEnd() {
		// RoundEndData generieren
		RoundEndData data = new RoundEndData();
		data.setOpeningHourNextDay(RandomUtils.getRandomNumber(6, 9));

		Log.debug("Server", "Runde zu Ende");

		// Alle Clienten informieren
		executor.submit(new AuthoritativeResultListenerThread() {
			@Override
			protected void informListener(
					AuthoritativeResultListener resultListener) {
				resultListener.onRoundEnd(data);
			}
		});

		// TODO Auch auf dem Server die neue Runde aufsetzen ->
		// RoundEndData anwenden
	}

	@Override
	public boolean readyUp(short networkId) {
		if (players.get(networkId).isReady()) {
			return false;
		}

		players.get(networkId).setReady(true);

		Log.info("Server", "Spieler %d ist für nächste Runde bereit",
				networkId);

		if (PlayerUtils.areAllPlayersReady(players.values()))
			startNextRoundForEveryone();

		return true;
	}

	public void startNextRoundForEveryone() {
		Log.info("Server", "Alle Spieler sind für die Runde bereit");

		for (LobbyPlayer player : players.values()) {
			player.setReady(false);
		}

		// Alle Clienten informieren
		executor.submit(new AuthoritativeResultListenerThread() {
			@Override
			protected void informListener(
					AuthoritativeResultListener resultListener) {
				resultListener.onAllPlayersReadied();
			}
		});

		startNextRound();
	}

	/**
	 * This runnable is used to easily inform every
	 * {@linkplain AuthoritativeSession#resultListeners result listener} on a
	 * separate thread.
	 */
	abstract class AuthoritativeResultListenerThread implements Runnable {
		@Override
		public void run() {
			for (AuthoritativeResultListener resultListener : resultListeners
					.values()) {
				informListener(resultListener);
			}
		}

		protected abstract void informListener(
				AuthoritativeResultListener resultListener);
	}

}
