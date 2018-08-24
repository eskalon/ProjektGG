package de.gg.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.base.Stopwatch;

import de.gg.game.data.GameDifficulty;
import de.gg.game.data.GameSessionSetup;
import de.gg.game.data.GameSpeed;
import de.gg.game.data.vote.VoteResults;
import de.gg.game.entity.Character;
import de.gg.game.entity.Player;
import de.gg.game.entity.Position;
import de.gg.game.system.ProcessingSystem;
import de.gg.game.system.smp.RoundEndSystem;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.game.vote.VoteableMatter;
import de.gg.game.world.City;
import de.gg.network.LobbyPlayer;
import de.gg.screen.GameVoteScreen;
import de.gg.util.CountdownTimer;
import de.gg.util.Log;
import de.gg.util.TickCounter;
import de.gg.util.TickCounter.TickHandler;

/**
 * This class holds the game data and takes care of processing the rounds
 * including the votes on the beginning of a new round.
 * <p>
 * Following are the different phases a game session can be in:
 * <ul>
 * <li>{@link #init(SavedGame)}: Initializes the game session, has to get called
 * before the first {@link #update()}-call</li>
 * <li>{@link #update()}: Processes the current round; has to get called
 * continually; returns <code>true</code> <i>once</i> when the current round is
 * over</li>
 * <li>{@link #startNextRound()}: Has to get called (internally) to start the
 * next round</li>
 * <li>{@link #finishCurrentVote(VoteResults)}: If there are any votes to hold
 * after {@link #startNextRound()} has been called, this method has to get
 * called to inform the session about its results</li>
 * </ul>
 *
 */
public abstract class GameSession {

	static final int ROUND_DURATION_IN_SECONDS = 35; // 8*60
	protected static final int TICKS_PER_SECOND = 10;

	protected static final int TICKS_PER_ROUND = ROUND_DURATION_IN_SECONDS
			* TICKS_PER_SECOND;
	private static final int TICK_DURATION = 1000
			* GameSpeed.NORMAL.getDeltaTimeMultiplied() / TICKS_PER_SECOND;

	private volatile int currentRound = 0;
	protected GameSpeed gameSpeed = GameSpeed.NORMAL;

	private TickCounter tickCounter;

	private Stopwatch logTimer = Stopwatch.createUnstarted();

	private volatile boolean initialized = false;

	/**
	 * Is set to <code>true</code> when the game is in the voting mode before
	 * the next round begins.
	 *
	 * @see GameVoteScreen the screen responsible for rendering the voting
	 *      process.
	 */
	private boolean holdVote = false;
	/**
	 * The matter a vote is currently held on.
	 */
	protected VoteableMatter matterToVoteOn = null;
	/**
	 * This countdown is used to time the end of a vote a few seconds after the
	 * data arrived. This allows the player to actually read the displayed data.
	 */
	private CountdownTimer voteTimer = new CountdownTimer();

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
	}

	/**
	 * Sets the game up by initializing the city, the game entities and the
	 * processing systems.
	 * <p>
	 * After the initialization the session can be updated via calling
	 * {@link #update()}. To resume the game after a round ended
	 * {@link #startNextRound()} has to get called.
	 *
	 * @param savedGame
	 *            <i>Not</i> <code>null</code> if this is a loaded game state.
	 */
	public synchronized void init(@Nullable SavedGame savedGame) {
		if (savedGame == null) {
			this.city = new City();
			this.city.generate(sessionSetup, players);
		} else {
			this.city = savedGame.city;
			this.currentRound = savedGame.currentRound;
			// Session- & server setup get set in the constructors

			// Switch player IDs when loading game
			if (savedGame != null) {
				for (Entry<Short, LobbyPlayer> newE : players.entrySet()) {
					for (Entry<Short, String> oldE : savedGame.clientIdentifiers
							.entrySet()) {
						if (newE.getValue().getHostname()
								.equals(oldE.getValue())) {
							// Use negative numbers so there are no collisions
							city.switchPlayerId(oldE.getKey(),
									(short) -newE.getKey());
						}
					}
				}
				// Revert the IDs back to positive numbers
				for (short s : savedGame.city.getPlayers().keySet()) {
					city.switchPlayerId(s, (short) -s);
				}
			}

			// TODO in den Konstruktoren die Setups setzen
			// TODO in der Lobby das Setup disablen
		}

		// Add and initialize the smp system(s)
		this.roundEndSystem = new RoundEndSystem(localNetworkId);
		this.roundEndSystem.init(city);

		// Init the tick counter
		this.tickCounter = new TickCounter(new TickHandler() {
			@Override
			public void onTick() {
				fixedUpdate();
			}

			@Override
			public int getDeltaMultiplier() {
				return gameSpeed.getDeltaTimeMultiplied();
			}
		}, TICKS_PER_ROUND, TICK_DURATION,
				savedGame == null ? 0 : savedGame.lastProcessedTick);

		this.initialized = true;
	}

	/**
	 * Updates the game session. The session has to get
	 * {@linkplain #init(SavedGame) initialized} before. Returns
	 * <code>true</code> <i>once</i>, when a round is over. To start the next
	 * round {@link #startNextRound()}.
	 *
	 * @return whether the in-game day is over (8 minutes).
	 */
	public synchronized boolean update() {
		if (!initialized) {
			Log.error(localNetworkId == -1 ? "Server" : "Client",
					"Die Session muss zuerst initialisiert werden, bevor sie geupdated werden kann");
			return false;
		}

		if (!holdVote) {
			// NORMAL UPDATE CYCLE
			if (tickCounter.update()) {
				processRoundEnd();
				return true;
			}
			return false;
		} else {
			// PROCESS VOTES
			if (matterToVoteOn == null) {
				matterToVoteOn = city.getMattersToHoldVoteOn().pollFirst();

				onNewVote(matterToVoteOn);

				if (matterToVoteOn == null) {
					holdVote = false;
				}
			} else {
				if (voteTimer.isRunning()) {
					// 7 Sekunden nach Vote-Ende warten
					if (voteTimer.update()) {
						voteTimer.reset();
						matterToVoteOn = null;
					}
				}
			}

			return false;
		}
	}

	private void processRoundEnd() {
		logTimer.reset().start();
		// Character
		for (Entry<Short, Character> e : city.getCharacters().entrySet()) {
			roundEndSystem.processCharacter(e.getKey(), e.getValue());
		}

		// Player
		for (Entry<Short, Player> e : city.getPlayers().entrySet()) {
			roundEndSystem.processPlayer(e.getKey(), e.getValue());
		}

		// Position
		for (Entry<PositionType, Position> e : city.getPositions().entrySet()) {
			roundEndSystem.processPosition(e.getKey(), e.getValue());
		}

		Log.info(localNetworkId == -1 ? "Server" : "Client",
				"RoundEndSystem in %d ms verarbeitet",
				logTimer.elapsed(Log.DEFAULT_TIME_UNIT));
	}

	/**
	 * This is used to process the game. It is called ten times per second if
	 * the game is running on {@linkplain GameSpeed#NORMAL normal speed}.
	 */
	protected synchronized void fixedUpdate() {
		if (isRightTick(TICKS_PER_SECOND)) {
			// PROCESSING SYSTEMS
			// Character
			for (ProcessingSystem<Character> sys : characterSystems) {
				if (sys.isProcessedContinuously() || (!sys.wasProcessed())) {
					if (isRightTick(sys.getTickRate())) {
						logTimer.reset().start();
						for (Entry<Short, Character> e : city.getCharacters()
								.entrySet()) {
							sys.process(e.getKey(), e.getValue());
						}
						Log.info(localNetworkId == -1 ? "Server" : "Client",
								"%s-System in %d ms verarbeitet",
								sys.getClass().getSimpleName(),
								logTimer.elapsed(Log.DEFAULT_TIME_UNIT));

						sys.setAsProcessed(true);
					}
				}
			}
			// Player
			for (ProcessingSystem<Player> sys : playerSystems) {
				if (sys.isProcessedContinuously() || (!sys.wasProcessed())) {
					if (isRightTick(sys.getTickRate())) {
						logTimer.reset().start();
						for (Entry<Short, Player> e : city.getPlayers()
								.entrySet()) {
							sys.process(e.getKey(), e.getValue());
						}
						Log.info(localNetworkId == -1 ? "Server" : "Client",
								"%s-System in %d ms verarbeitet",
								sys.getClass().getSimpleName(),
								logTimer.elapsed(Log.DEFAULT_TIME_UNIT));

						sys.setAsProcessed(true);
					}
				}
			}
		}
	}

	/**
	 * Called after a round ended to start the next round. If there are any
	 * {@linkplain City#getMattersToHoldVoteOn() matters to hold a vote on}
	 * those are done first.
	 */
	protected synchronized void startNextRound() {
		currentRound++;

		Log.debug(localNetworkId == -1 ? "Server" : "Client",
				"Runde %s gestartet; Letzte Runde wurden %s Ticks gezählt",
				currentRound, tickCounter.getTickCount());

		// Reset the tick counter
		tickCounter.reset();

		// Reset the processing systems
		for (ProcessingSystem<Character> sys : characterSystems) {
			if (!sys.isProcessedContinuously())
				sys.setAsProcessed(false);
		}
		for (ProcessingSystem<Player> sys : playerSystems) {
			if (!sys.isProcessedContinuously())
				sys.setAsProcessed(false);
		}

		// Take care of votes
		Log.info(localNetworkId == -1 ? "Server" : "Client",
				"Es stehen %d Tagesordnunspunkte an",
				city.getMattersToHoldVoteOn().size());
		holdVote = !city.getMattersToHoldVoteOn().isEmpty();
	}

	/**
	 * Has to get called to finish the current vote and go on to the next one.
	 *
	 * @param result
	 *            the result of the vote.
	 */
	protected void finishCurrentVote(VoteResults result) {
		voteTimer.start(7000);

		matterToVoteOn.processVoteResult(result, city);
	}

	/**
	 * Is called by the session when a new vote is started.
	 *
	 * @param matterToVoteOn
	 *            The new matter to vote on. Is <code>null</code> when the
	 *            voting process is over.
	 */
	protected abstract void onNewVote(VoteableMatter matterToVoteOn);

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
		return tickCounter.getTickCount() % tickInterval == 0;
	}

	/**
	 * @return the current tick count. <code>-1</code> if the session didn't get
	 *         {@linkplain #init(SavedGame) initialized}.
	 */
	protected int getTickCount() {
		return tickCounter == null ? -1 : tickCounter.getTickCount();
	}

	protected void setGameSpeed(GameSpeed gameSpeed) {
		this.gameSpeed = gameSpeed;
	}

	/**
	 * @return the current round.
	 */
	public int getCurrentRound() {
		return currentRound;
	}

	/**
	 * @return the progress of the current round (in a range of <code>0</code>
	 *         to <code>1</code>).
	 */
	public float getRoundProgress() {
		return tickCounter.getTickCount() / (float) TICKS_PER_ROUND;
	}

	protected long getGameSeed() {
		return sessionSetup.getSeed();
	}

	public long getRandomSeedForCurrentRound() {
		return sessionSetup.getSeed() + currentRound;
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

}
