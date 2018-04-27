package de.gg.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.gg.core.ProjektGG;
import de.gg.event.ChangedGameSpeedEvent;
import de.gg.event.RoundEndEvent;
import de.gg.game.data.GameSessionSetup;
import de.gg.game.data.GameSpeed;
import de.gg.game.data.NotificationData;
import de.gg.game.data.RoundEndData;
import de.gg.game.system.ProcessingSystem;
import de.gg.game.system.client.FirstEventWaveClientSystem;
import de.gg.network.LobbyPlayer;
import de.gg.screen.GameRoundendScreen;
import de.gg.util.Log;

/**
 * This class simulates a game session on the client of a multiplayer game. It
 * is also implementing the interface used for the RMI by the server.
 */
public class SlaveSession extends GameSession
		implements AuthoritativeResultListener {

	private ProjektGG game;
	private List<NotificationData> notifications = new ArrayList<>();

	private GameClock clock;

	/**
	 * Creates a new multiplayer session.
	 * 
	 * @param game
	 *            an instance of the game.
	 * @param sessionSetup
	 *            the settings of the game session.
	 * @param players
	 *            a hashmap containing the players.
	 * @param networkID
	 *            the networkID of the local player.
	 * @param eventBus
	 *            the game's event bus.
	 */
	public SlaveSession(ProjektGG game, GameSessionSetup sessionSetup,
			HashMap<Short, LobbyPlayer> players, short networkID) {
		super(sessionSetup, players, networkID);
		this.game = game;
		this.clock = new GameClock(game.getEventBus());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setupGame() {
		super.setupGame();

		// Setup the client systems
		ProcessingSystem s;
		s = new FirstEventWaveClientSystem();
		s.init(city, getGameSeed());
		this.playerSystems.add(s);
	}

	@Override
	public synchronized void fixedUpdate() {
		super.fixedUpdate();

		if (isRightTick(TICKS_PER_SECOND)) {
			clock.update();
		}

		if (isRightTick(15)) {
			Log.debug("CLOCK", "%02d:%02d", getClock().getHour(),
					getClock().getMinute());
		}
	}

	@Override
	protected synchronized void startNextRound() {
		super.startNextRound();

		clock.resetClock();
	}

	public List<NotificationData> getNotifications() {
		return notifications;
	}

	@Override
	public synchronized void onAllPlayersReadied() {
		Log.debug("Client", "Alle Spieler sind bereit! Nächste Runde startet");

		this.startNextRound();
		((GameRoundendScreen) game.getScreen("roundEnd")).setData(null);
		game.pushScreen("map");
	}

	@Override
	public void onRoundEnd(RoundEndData data) { // Inherited from
												// AuthoritativeResultListener
		game.getEventBus().post(new RoundEndEvent(data));

		// Process the last round
		super.processRoundEnd(data); // Inherited from GameSession
	}

	@Override
	public void onCharacterDeath(short characterId) {
		// TODO Todeseffekte (siehe
		// FirstCharacterEventWaveServerSystem#process())

		// außerdem wenn lokaler Spieler oder Verwandter dann
		// Notification-Banner (-> Banner-Event das UI Reloaded für
		// MapScreen; Notification-List in GameSession)
	}

	@Override
	public void onCharacterDamage(short characterId, short damage) {
		city.getCharacters().get(characterId)
				.setHp(city.getCharacters().get(characterId).getHp() - damage);

	}

	@Override
	public void onPlayerIllnessChange(short playerId, boolean isIll) {
		city.getPlayers().get(playerId).setIll(isIll);
	}

	@Override
	public void setGameSpeed(int index) {
		setGameSpeed(GameSpeed.values()[index]);

		game.getEventBus().post(new ChangedGameSpeedEvent(gameSpeed));
	}

	public GameClock getClock() {
		return clock;
	}

}
