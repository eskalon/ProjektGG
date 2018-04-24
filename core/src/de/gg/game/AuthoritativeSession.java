package de.gg.game;

import java.util.HashMap;

import de.gg.game.data.GameSessionSetup;
import de.gg.game.data.GameSpeed;
import de.gg.game.data.RoundEndData;
import de.gg.game.system.ProcessingSystem;
import de.gg.game.system.server.FirstCharacterEventWaveServerSystem;
import de.gg.game.system.server.FirstPlayerEventWaveServerSystem;
import de.gg.game.system.server.IllnessDamageSystem;
import de.gg.game.system.server.NpcActionSystem;
import de.gg.game.system.server.NpcActionSystem2;
import de.gg.network.GameServer;
import de.gg.network.LobbyPlayer;
import de.gg.network.ServerSetup;
import de.gg.util.Log;
import de.gg.util.PlayerUtils;
import de.gg.util.RandomUtils;

/**
 * This class takes care of simulating the game session on the server side and
 * implements the {@linkplain SlaveActionHandler interface} used in the RMI for
 * the client.
 */
public class AuthoritativeSession extends GameSession
		implements SlaveActionListener {

	private HashMap<Short, AuthoritativeResultListener> resultListeners;
	private AuthoritativeResultListener resultListenerStub;

	private ServerSetup serverSetup;

	/**
	 * Creates a new multiplayer session.
	 * 
	 * @param sessionSetup
	 *            the settings of the game session.
	 * @param players
	 *            a hashmap containing the players.
	 * @param localNetworkId
	 *            the local player's network id.
	 */
	public AuthoritativeSession(GameSessionSetup sessionSetup,
			ServerSetup serverSetup, HashMap<Short, LobbyPlayer> players) {
		super(sessionSetup, players, (short) -1);

		this.resultListenerStub = new ServerAuthoritativResultListenerStub(
				this);
		this.serverSetup = serverSetup;
	}

	public void setResultListeners(
			HashMap<Short, AuthoritativeResultListener> resultListeners) {
		this.resultListeners = resultListeners;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setupGame() {
		for (LobbyPlayer player : players.values()) {
			player.setReady(false);
		}

		super.setupGame();

		// Setup the server processing systems
		ProcessingSystem s;
		s = new FirstCharacterEventWaveServerSystem(this);
		s.init(city, getGameSeed());
		this.characterSystems.add(s);

		s = new FirstPlayerEventWaveServerSystem(this);
		s.init(city, getGameSeed());
		this.playerSystems.add(s);

		s = new IllnessDamageSystem(this);
		s.init(city, getGameSeed());
		this.playerSystems.add(s);

		s = new NpcActionSystem(this);
		s.init(city, getGameSeed());
		this.characterSystems.add(s);

		s = new NpcActionSystem2(this);
		s.init(city, getGameSeed());
		this.characterSystems.add(s);
	}

	public void saveGame() {
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

		// Inform the clients
		resultListenerStub.onRoundEnd(data);

		// Process the last round
		super.processRoundEnd(data);

		// Save the stats
		saveStats();
	}

	public void saveStats() {
		// TODO generate & save stats
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

		resultListenerStub.onAllPlayersReadied();

		startNextRound();
	}

	/**
	 * @return the result listener stub used to distribute events to all
	 *         clients.
	 */
	public AuthoritativeResultListener getResultListenerStub() {
		return resultListenerStub;
	}

	/**
	 * @return a hashmap of all registered result listeners.
	 */
	public HashMap<Short, AuthoritativeResultListener> getResultListeners() {
		return resultListeners;
	}

	@Override
	public void increaseGameSpeed(short clientId) {
		if (!serverSetup.isHostOnlyCommands()
				|| clientId == GameServer.HOST_PLAYER_NETWORK_ID) {
			int index = gameSpeed.ordinal() + 1;

			gameSpeed = GameSpeed
					.values()[index >= GameSpeed.values().length ? 0 : index];
			resultListenerStub.setGameSpeed(gameSpeed.ordinal());
		}
	}

	@Override
	public void decreaseGameSpeed(short clientId) {
		if (!serverSetup.isHostOnlyCommands()
				|| clientId == GameServer.HOST_PLAYER_NETWORK_ID) {
			int index = gameSpeed.ordinal() - 1;

			gameSpeed = GameSpeed.values()[index < 0
					? GameSpeed.values().length - 1
					: index];
			resultListenerStub.setGameSpeed(gameSpeed.ordinal());
		}
	}

}
