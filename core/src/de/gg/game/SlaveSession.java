package de.gg.game;

import java.util.HashMap;

import de.gg.core.ProjektGG;
import de.gg.data.GameSessionSetup;
import de.gg.data.RoundEndData;
import de.gg.event.RoundEndEvent;
import de.gg.network.LobbyPlayer;
import de.gg.screen.GameRoundendScreen;
import de.gg.util.Log;

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
		super(sessionSetup, players);
		this.localId = networkID;
		this.game = game;
	}

	/**
	 * Currently unused.
	 */
	public void startGame() {
	}

	@Override
	public synchronized void onAllPlayersReadied() {
		Log.debug("Client", "Alle Spieler sind bereit! Nächste Runde startet");

		this.startNextRound();
		((GameRoundendScreen) game.getScreen("roundEnd")).setData(null);
		game.pushScreen("map");
	}

	@Override
	public void onRoundEnd(RoundEndData data) {
		// TODO apply round end data
		game.getEventBus().post(new RoundEndEvent(data));
	}

}
