package de.gg.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import de.gg.game.data.GameDifficulty;
import de.gg.game.data.GameSessionSetup;
import de.gg.game.data.GameSpeed;
import de.gg.game.data.RoundEndData;
import de.gg.game.entity.Character;
import de.gg.game.entity.City;
import de.gg.game.entity.Player;
import de.gg.game.system.ProcessingSystem;
import de.gg.game.system.smp.RoundEndSystem;
import de.gg.network.LobbyPlayer;
import de.gg.util.Log;
import de.gg.util.MeasuringUtil;

/**
 * This class holds the game data and takes care of processing the rounds.
 * <p>
 * Following are the different phases a game session can be in:
 * <ul>
 * <li>{@link #setupGame()}: Initializes the game session, has to get called
 * before the first {@link #update()}-call</li>
 * <li>{@link #update()}: Processes the current round; has to get called
 * continually; returns <code>true</code> when the current round is over</li>
 * <li>{@link #processRoundEnd(RoundEndData)}: Has to get called (internally) to
 * set up the next round after a round ended</li>
 * <li>{@link #startNextRound()}: Has to get called (internally) to start the
 * next round</li>
 * </ul>
 * 
 */
public abstract class GameSession {

	private static final long ROUND_DURATION_IN_SECONDS = 35; // 8*60
	static final long ROUND_DURATION = ROUND_DURATION_IN_SECONDS * 1000
			* GameSpeed.NORMAL.getDeltaTimeMultiplied();
	/**
	 * Set to true when a game round is over. The next round starts, when
	 * {{@link #startNextRound()}} is called.
	 */
	protected volatile boolean waitingForNextRound = false;

	/**
	 * Used to calculate the time delta.
	 */
	private long lastTime = System.currentTimeMillis();
	/**
	 * Used to determine when the current round ends and the end round screen is
	 * shown. To start the following round, the server needs to receive every
	 * player's approval.
	 */
	private volatile long currentRoundTime = ROUND_DURATION;
	private volatile int currentRound = 0;
	protected GameSpeed gameSpeed = GameSpeed.NORMAL;

	/**
	 * This time is used to calculate the update ticks.
	 */
	private volatile long updateTime;
	private static final int TICKS_PER_SECOND = 10;
	private static final long TICK_DURATION = 1000 / TICKS_PER_SECOND;
	/**
	 * The current tick. Is used to calculate whether an action should get
	 * executed.
	 * 
	 * @see #isRightTick(int)
	 */
	private volatile int currentTick = 0;

	private volatile boolean initialized = false;

	private GameClock clock;

	protected City city;
	protected HashMap<Short, LobbyPlayer> players;
	private GameSessionSetup sessionSetup;

	/**
	 * The network ID of the local player.
	 */
	protected short localNetworkId;

	protected ArrayList<ProcessingSystem<Character>> characterSystems = new ArrayList<>();
	protected ArrayList<ProcessingSystem<Player>> playerSystems = new ArrayList<>();

	private RoundEndSystem roundEndSystem;

	private MeasuringUtil measuringUtil = new MeasuringUtil();

	/**
	 * Creates a new game session.
	 * 
	 * @param sessionSetup
	 *            the settings of the game session.
	 * @param players
	 *            a hashmap containing the players.
	 */
	public GameSession(GameSessionSetup sessionSetup,
			HashMap<Short, LobbyPlayer> players, short localNetworkId) {
		this.sessionSetup = sessionSetup;
		this.players = players;
		this.localNetworkId = localNetworkId;
		this.city = new City();
		this.clock = new GameClock();
	}

	/**
	 * Sets the city, the game entities and the processing systems up.
	 * <p>
	 * To update the session {@link #update()} has to get called. To resume the
	 * game after a round ended {@link #startNextRound()} has to get called.
	 */
	public synchronized void setupGame() {
		this.city.generate(sessionSetup, players);
		this.initialized = true;

		// Add and initialize the smp systems
		this.roundEndSystem = new RoundEndSystem(localNetworkId);
		this.roundEndSystem.init(city);
	}

	/**
	 * Updates the game session. Returns true once, when a round is over. To
	 * start the next round call {@link #startNextRound()}.
	 * 
	 * @return whether the ingame day is over (8 minutes).
	 */
	public synchronized boolean update() {
		// Zeit-Delta ermitteln
		long currentTime = System.currentTimeMillis();
		long delta = (currentTime - lastTime)
				* gameSpeed.getDeltaTimeMultiplied();
		lastTime = currentTime;

		if (!initialized) {
			Log.error(localNetworkId == -1 ? "Server" : "Client",
					"The session has to get initialized first before it can be updated");
			return false;
		}

		// Rundenzeit hochzählen
		currentRoundTime += delta;

		if (currentRoundTime >= ROUND_DURATION) {
			// Runde zuende
			if (!waitingForNextRound) {
				waitingForNextRound = true;

				if (getRoundProgress() < 1)
					for (int i = 0; i < 6; i++)
						Log.error(localNetworkId == -1 ? "Server" : "Client",
								"Thread-Bug in Runde %s", getCurrentRound());

				return true;

			}
		} else { // durch das else hier können (bei großen deltas) einige Ticks
					// wegfallen
			// Runde läuft noch
			updateTime += delta;

			if (updateTime >= TICK_DURATION) {
				updateTime -= TICK_DURATION;
				currentTick++;
				fixedUpdate();
			}
		}

		return false;
	}

	protected void setGameSpeed(GameSpeed gameSpeed) {
		this.gameSpeed = gameSpeed;
	}

	/**
	 * This is used to process the game. It is called ten times per second if
	 * the game is running on normal speed.
	 */
	public synchronized void fixedUpdate() {
		if (isRightTick(10)) {
			clock.update();

			// PROCESSING SYSTEMS
			// Character
			for (ProcessingSystem<Character> sys : characterSystems) {
				if (sys.isProcessedContinuously() || (!sys.wasProcessed())) {
					if (isRightTick(sys.getTickRate())) {
						measuringUtil.start();
						for (Entry<Short, Character> e : city.getCharacters()
								.entrySet()) {
							sys.process(e.getKey(), e.getValue());
						}
						Log.info(localNetworkId == -1 ? "Server" : "Client",
								"Processed the %s-System in %d miliseconds",
								sys.getClass().getSimpleName(),
								measuringUtil.stop());

						if (!sys.isProcessedContinuously()) {
							sys.setAsProcessed(true);
						}
					}
				}
			}
			// Player
			for (ProcessingSystem<Player> sys : playerSystems) {
				if (sys.isProcessedContinuously() || (!sys.wasProcessed())) {
					if (isRightTick(sys.getTickRate())) {
						measuringUtil.start();
						for (Entry<Short, Player> e : city.getPlayers()
								.entrySet()) {
							sys.process(e.getKey(), e.getValue());
						}
						Log.info(localNetworkId == -1 ? "Server" : "Client",
								"Processed the %s-System in %d miliseconds",
								sys.getClass().getSimpleName(),
								measuringUtil.stop());

						if (!sys.isProcessedContinuously()) {
							sys.setAsProcessed(true);
						}
					}
				}
			}
		}
	}

	/**
	 * Called after a round ended to setup the next round.
	 * 
	 * @param data
	 */
	protected void processRoundEnd(RoundEndData data) {
		measuringUtil.start();
		// Character
		for (Entry<Short, Character> e : city.getCharacters().entrySet()) {
			roundEndSystem.processCharacter(e.getKey(), e.getValue());
		}

		// Player
		for (Entry<Short, Player> e : city.getPlayers().entrySet()) {
			roundEndSystem.processPlayer(e.getKey(), e.getValue());
		}
		Log.info(localNetworkId == -1 ? "Server" : "Client",
				"Processed the RoundEnd-System in %d miliseconds",
				measuringUtil.stop());
	}

	/**
	 * Returns whether the specified interval is over and an action should get
	 * executed.
	 * 
	 * @param tickInterval
	 *            the interval after which this method is supposed to return
	 *            true.
	 * @return <code>true</code> after the specified tick interval is over.
	 */
	protected boolean isRightTick(int tickInterval) {
		return currentTick % tickInterval == 0;
	}

	/**
	 * Called after a round ended to start the next round.
	 */
	protected synchronized void startNextRound() {
		currentRound++;
		currentRoundTime = 0;
		lastTime = System.currentTimeMillis();
		updateTime = 0;
		currentTick = 0;
		clock.resetClock();

		// Reset the processing systems
		for (ProcessingSystem<Character> sys : characterSystems) {
			if (!sys.isProcessedContinuously())
				sys.setAsProcessed(false);
		}

		waitingForNextRound = false;
	}

	/**
	 * @return the current round.
	 */
	public int getCurrentRound() {
		return currentRound;
	}

	/**
	 * @return the progress of the current round (in a range of 0 to 1). Can be
	 *         >1 right after a round ended and before the next round started.
	 */
	public float getRoundProgress() {
		return currentRoundTime / (float) ROUND_DURATION;
	}

	public long getGameSeed() {
		return sessionSetup.getSeed();
	}

	/**
	 * @return the current round.
	 */
	public int getRound() {
		return currentRound;
	}

	/**
	 * @return the game's difficulty.
	 */
	public GameDifficulty getDifficulty() {
		return sessionSetup.getDifficulty();
	}

	public City getCity() {
		return city;
	}

	public GameClock getClock() {
		return clock;
	}

}
