package de.gg.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import de.gg.data.GameSessionSetup;
import de.gg.data.GameSessionSetup.GameDifficulty;
import de.gg.data.RoundEndData;
import de.gg.game.entity.Character;
import de.gg.game.entity.City;
import de.gg.game.entity.Player;
import de.gg.game.system.ProcessingSystem;
import de.gg.game.system.smp.RoundEndSystem;
import de.gg.network.LobbyPlayer;
import de.gg.util.Log;
import de.gg.util.MeasuringUtil;

/**
 * This class holds the game data and takes care of calculating when a round
 * ends.
 */
public abstract class GameSession {

	private static final long ROUND_DURATION = 35 * 1000; // 8*60*1000
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
		long delta = currentTime - lastTime;
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
		} else {
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

	/**
	 * This class takes care of translating the update ticks to the in-game
	 * time. Every ten ticks {@link #update()} has to get called.
	 */
	public class GameClock {

		/**
		 * The duration of an in-game day in in-game hours.
		 */
		private static final int HOURS_PER_DAY = 17;
		/**
		 * The hour the in-game day starts.
		 */
		private static final int STARTING_HOUR = 6;
		/**
		 * The in-game minutes passing per update.
		 */
		private static final float MINUTES_PER_UPDATE = HOURS_PER_DAY * 60
				/ (ROUND_DURATION / 1000);

		private int minute, hour;

		/**
		 * Should get called every ten ticks (= 1 second). Updates the clock.
		 */
		protected void update() {
			minute += MINUTES_PER_UPDATE;

			if (minute >= 60) {
				minute -= 60;
				hour++;
			}
		}

		/**
		 * Resets the clock.
		 */
		protected void resetClock() {
			hour = STARTING_HOUR;
			minute = 0;
		}

		public int getHour() {
			return hour;
		}

		public int getMinute() {
			return minute;
		}

	}

}
