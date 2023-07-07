package de.gg.game.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import de.damios.guacamole.Stopwatch;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.gg.game.misc.CountdownTimer;
import de.gg.game.misc.TickCounter;
import de.gg.game.misc.TickCounter.TickHandler;
import de.gg.game.model.World;
import de.gg.game.model.entities.Character;
import de.gg.game.model.entities.Player;
import de.gg.game.model.entities.Position;
import de.gg.game.model.types.GameSpeed;
import de.gg.game.model.types.PositionType;
import de.gg.game.model.votes.Ballot;
import de.gg.game.model.votes.BallotResults;
import de.gg.game.network.PlayerData;
import de.gg.game.systems.ProcessingSystem;
import de.gg.game.systems.smp.RoundEndSystem;
import de.gg.game.ui.screens.GameBallotScreen;

/**
 * This class holds the game data and takes care of processing the rounds
 * including the votes on the beginning of a new round.
 * <p>
 * Following are the different phases a game session can be in:
 * <ul>
 * <li>{@link #init(HashMap, SavedGame)}: Initializes the game session, has to
 * get called before the first {@link #update()}-call</li>
 * <li>{@link #update()}: Processes the current round; has to get called
 * continually; returns <code>true</code> <i>once</i> when the current round is
 * over</li>
 * <li>{@link #startNextRound()}: Has to get called (internally) to start the
 * next round</li>
 * <li>{@link #finishCurrentVote(BallotResults)}: If there are any votes to hold
 * after {@link #startNextRound()} has been called, this method has to get
 * called to inform the session about its results</li>
 * </ul>
 *
 */
public abstract class GameSession {

	private static final Logger LOG = LoggerService
			.getLogger(GameSession.class);

	public static final int ROUND_DURATION_IN_SECONDS = 35; // 8*60
	protected static final int TICKS_PER_SECOND = 10;

	public static final int TICKS_PER_ROUND = ROUND_DURATION_IN_SECONDS
			* TICKS_PER_SECOND;
	private static final int TICK_DURATION = 1000
			* GameSpeed.NORMAL.getDeltaTimeMultiplied() / TICKS_PER_SECOND;

	private volatile int currentRound = 0;

	protected GameSpeed gameSpeed = GameSpeed.NORMAL;

	protected TickCounter tickCounter;

	private Stopwatch logTimer = Stopwatch.createUnstarted();

	private volatile boolean initialized = false;

	/**
	 * Is set to <code>true</code> when the game is in the voting mode before
	 * the next round begins.
	 *
	 * @see GameBallotScreen the screen responsible for rendering the voting
	 *      process.
	 */
	private boolean holdVote = false;
	/**
	 * The matter a vote is currently held on.
	 */
	protected Ballot matterToVoteOn = null;
	/**
	 * This countdown is used to time the end of a vote a few seconds after the
	 * data arrived. This allows the player to actually read the displayed data.
	 */
	private CountdownTimer voteTimer = new CountdownTimer();

	protected World world;
	protected GameSessionSetup sessionSetup;

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
	 * @param localNetworkId
	 *            the local network ID.
	 */
	public GameSession(GameSessionSetup sessionSetup, short localNetworkId) {
		this.sessionSetup = sessionSetup;
		this.localNetworkId = localNetworkId;
	}

	/**
	 * Sets the game up by initializing the world, the game entities and the
	 * processing systems.
	 * <p>
	 * After the initialization the session can be updated via calling
	 * {@link #update()}. To resume the game after a round ended
	 * {@link #startNextRound()} has to get called.
	 *
	 * @param players
	 *            A hashmap of all players in this session.
	 * @param savedGame
	 *            <i>Not</i> <code>null</code> if this is a loaded game state.
	 */
	public synchronized void init(HashMap<Short, PlayerData> players,
			@Nullable SavedGame savedGame) {
		if (savedGame == null) {
			this.world = new World();
			this.world.generate(sessionSetup, players);
		} else {
			this.world = savedGame.world;
			this.currentRound = savedGame.currentRound;
			// Session- & server setup get set in the constructors

			// Switch player IDs when loading game
			if (savedGame != null) {
				for (Entry<Short, PlayerData> newE : players.entrySet()) {
					for (Entry<Short, String> oldE : savedGame.clientIdentifiers
							.entrySet()) {
						if (newE.getValue().getHostname()
								.equals(oldE.getValue())) {
							// Change all mentions of the saved player id to the
							// new one
							// Use negative numbers so there are no collisions
							Player p = world.getPlayers().remove(oldE.getKey());
							world.getPlayers().put((short) -newE.getKey(), p);
						}
					}
				}
				// Revert the IDs back to positive numbers
				for (short s : savedGame.world.getPlayers().keySet()) {
					Player p = world.getPlayers().remove(s);
					world.getPlayers().put((short) -s, p);
				}
			}

			// TODO in den Konstruktoren die Setups setzen
			// TODO in der Lobby das Setup disablen
		}

		// Add and initialize the smp system(s)
		this.roundEndSystem = new RoundEndSystem(localNetworkId);
		this.roundEndSystem.init(world);

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
			LOG.error((localNetworkId == -1 ? "[SERVER] " : "[CLIENT] ")
					+ "Die Session muss zuerst initialisiert werden, bevor sie geupdated werden kann");
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
				matterToVoteOn = world.getMattersToHoldVoteOn().pollFirst();

				onNewBallot(matterToVoteOn);

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
		for (Entry<Short, Character> e : world.getCharacters().entrySet()) {
			roundEndSystem.processCharacter(e.getKey(), e.getValue());
		}

		// Player
		for (Entry<Short, Player> e : world.getPlayers().entrySet()) {
			roundEndSystem.processPlayer(e.getKey(), e.getValue());
		}

		// Position
		for (Entry<PositionType, Position> e : world.getPositions()
				.entrySet()) {
			roundEndSystem.processPosition(e.getKey(), e.getValue());
		}

		LOG.info(
				(localNetworkId == -1 ? "[SERVER] " : "[CLIENT] ")
						+ "RoundEndSystem in %d ms verarbeitet",
				logTimer.getTime(TimeUnit.MILLISECONDS));
	}

	/**
	 * This is used to process the game. It is called ten times per second if
	 * the game is running on {@linkplain GameSpeed#NORMAL normal speed}.
	 */
	protected synchronized void fixedUpdate() {
		if (tickCounter.isRightTick(5)) {
			// PROCESSING SYSTEMS
			// Character
			for (ProcessingSystem<Character> sys : characterSystems) {
				if (sys.isProcessedContinuously() || (!sys.wasProcessed())) {
					if (tickCounter.isRightTick(sys.getTickRate())) {
						logTimer.reset().start();
						for (Entry<Short, Character> e : world.getCharacters()
								.entrySet()) {
							sys.process(e.getKey(), e.getValue());
						}
						LOG.info(
								(localNetworkId == -1 ? "[SERVER] "
										: "[CLIENT] ")
										+ "%s-System in %d ms verarbeitet",
								sys.getClass().getSimpleName(),
								logTimer.getTime(TimeUnit.MILLISECONDS));

						sys.setAsProcessed(true);
					}
				}
			}
			// Player
			for (ProcessingSystem<Player> sys : playerSystems) {
				if (sys.isProcessedContinuously() || (!sys.wasProcessed())) {
					if (tickCounter.isRightTick(sys.getTickRate())) {
						logTimer.reset().start();
						for (Entry<Short, Player> e : world.getPlayers()
								.entrySet()) {
							sys.process(e.getKey(), e.getValue());
						}
						LOG.info(
								(localNetworkId == -1 ? "[SERVER] "
										: "[CLIENT] ")
										+ "%s-System in %d ms verarbeitet",
								sys.getClass().getSimpleName(),
								logTimer.getTime(TimeUnit.MILLISECONDS));

						sys.setAsProcessed(true);
					}
				}
			}
		}
	}

	/**
	 * Has to get called after a round ended to start the next round. If there
	 * are any {@linkplain World#getMattersToHoldVoteOn() matters to hold a vote
	 * on} those are done first.
	 */
	public synchronized void startNextRound() {
		currentRound++;

		LOG.debug((localNetworkId == -1 ? "[SERVER] " : "[CLIENT] ")
				+ "Runde %s gestartet; Letzte Runde wurden %s Ticks gezählt",
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
		LOG.info(
				(localNetworkId == -1 ? "[SERVER] " : "[CLIENT] ")
						+ "Es stehen %d Tagesordnunspunkte an",
				world.getMattersToHoldVoteOn().size());
		holdVote = !world.getMattersToHoldVoteOn().isEmpty();
	}

	/**
	 * Has to get called to finish the current vote and go on to the next one.
	 *
	 * @param result
	 *            the result of the vote.
	 */
	public void finishCurrentVote(BallotResults result) {
		voteTimer.start(7000);

		matterToVoteOn.processVoteResult(result, world);
	}

	/**
	 * Is called by the session when a new ballot is started.
	 *
	 * @param ballot
	 *            the new ballot to vote on; is {@code null} when the voting
	 *            process is over
	 */
	protected abstract void onNewBallot(@Nullable Ballot ballot);

	/**
	 * @return the current tick count. <code>-1</code> if the session didn't get
	 *         {@linkplain #init(SavedGame) initialized}.
	 */
	public int getTickCount() {
		return tickCounter == null ? -1 : tickCounter.getTickCount();
	}

	public GameSpeed getGameSpeed() {
		return gameSpeed;
	}

	public void setGameSpeed(GameSpeed gameSpeed) {
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

	public long getRandomSeedForCurrentRound() {
		return sessionSetup.getSeed() + currentRound;
	}

	public Ballot getMatterToVoteOn() {
		return matterToVoteOn;
	}

	/**
	 * @return the setup of the session including the map, seed and difficulty.
	 */
	public GameSessionSetup getSessionSetup() {
		return sessionSetup;
	}

	public World getWorld() {
		return world;
	}

}
