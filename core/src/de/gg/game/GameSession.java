package de.gg.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.google.common.base.Stopwatch;

import de.gg.game.data.GameDifficulty;
import de.gg.game.data.GameSessionSetup;
import de.gg.game.data.GameSpeed;
import de.gg.game.data.RoundEndData;
import de.gg.game.data.vote.ElectionVote;
import de.gg.game.data.vote.ImpeachmentVote;
import de.gg.game.data.vote.VoteOption;
import de.gg.game.data.vote.VoteResults;
import de.gg.game.data.vote.VoteableMatter;
import de.gg.game.entity.Character;
import de.gg.game.entity.Player;
import de.gg.game.entity.Position;
import de.gg.game.system.ProcessingSystem;
import de.gg.game.system.smp.RoundEndSystem;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.game.world.City;
import de.gg.network.LobbyPlayer;
import de.gg.screen.GameVoteScreen;
import de.gg.util.Log;

/**
 * This class holds the game data and takes care of processing the rounds
 * including the votes on the beginning of a new round.
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
 * <li>{@link #finishCurrentVote(VoteResults)()}: If there are any votes to hold
 * after {@link #startNextRound()} has been called, this method has to get
 * called to inform the session about its results</li>
 * </ul>
 * 
 */
public abstract class GameSession {

	static final int ROUND_DURATION_IN_SECONDS = 35; // 8*60

	static final int ROUND_DURATION = ROUND_DURATION_IN_SECONDS * 1000
			* GameSpeed.NORMAL.getDeltaTimeMultiplied();
	/**
	 * Set to true when a game round is over. The next round starts, when
	 * {{@link #startNextRound()}} is called.
	 */
	protected volatile boolean waitingForNextRound = false;

	/**
	 * Used to calculate the time delta.
	 */
	private long lastTime = -1;
	/**
	 * Used to determine when the current round ends and the end round screen is
	 * shown. To start the following round, the server needs to receive every
	 * player's approval.
	 */
	private volatile long currentRoundTime = ROUND_DURATION;
	private volatile int currentRound = 0;
	protected GameSpeed gameSpeed = GameSpeed.NORMAL;

	protected static final int TICKS_PER_SECOND = 10;

	private static final int TICKS_PER_ROUND = ROUND_DURATION_IN_SECONDS
			* TICKS_PER_SECOND;
	private static final int TICK_DURATION = 1000
			* GameSpeed.NORMAL.getDeltaTimeMultiplied() / TICKS_PER_SECOND;
	/**
	 * This time is used to calculate the update ticks.
	 */
	private volatile long updateTime = 0;

	/**
	 * The current tick. Is used to calculate whether an action should get
	 * executed.
	 * 
	 * @see #isRightTick(int)
	 */
	private volatile int currentTick = TICKS_PER_ROUND;

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
	 * This variable is used to time the end of a vote a few seconds after the
	 * data arrived. This allows the data to be displayed to the player.
	 */
	private float voteFinishedTimer = -1;

	private volatile boolean initialized = false;

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

	private Stopwatch timer = Stopwatch.createUnstarted();

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
	 */
	public synchronized void setupGame(SavedGame savedGame) {
		if (savedGame == null) {
			this.city = new City();
			this.city.generate(sessionSetup, players);
		} else {
			this.city = savedGame.city;
			// TODO Momentanen Rundenzeitpunkt aufsetzen
		}

		this.initialized = true;

		// Add and initialize the smp systems
		this.roundEndSystem = new RoundEndSystem(localNetworkId);
		this.roundEndSystem.init(city);
	}

	/**
	 * Updates the game session. The session has to get {@linkplain #setupGame()
	 * setup} before. Returns <code>true</code> once, when a round is over. To
	 * start the next round call {@link #startNextRound()}.
	 * 
	 * @return whether the ingame day is over (8 minutes).
	 */
	public synchronized boolean update() {
		if (!initialized) {
			Log.error(localNetworkId == -1 ? "Server" : "Client",
					"Die Session muss zuerst initialisiert werden, bevor sie geupdated werden kann");
			return false;
		}

		if (!holdVote) {
			// NORMAL UPDATE CYCLE
			// Die Zeit für das erste Update setzen
			if (lastTime == -1)
				lastTime = System.currentTimeMillis();

			// Zeit-Delta ermitteln
			long currentTime = System.currentTimeMillis();
			long delta = (currentTime - lastTime)
					* gameSpeed.getDeltaTimeMultiplied();
			lastTime = currentTime;

			// Rundenzeit hochzählen
			currentRoundTime += delta;
			updateTime += delta;

			// Ticks berechnen
			while (currentTick < TICKS_PER_ROUND
					&& updateTime >= TICK_DURATION) {
				// Neuer Update Tick
				updateTime -= TICK_DURATION;
				currentTick++;
				fixedUpdate();
			}

			// Rundenende berechnen
			if (currentRoundTime >= ROUND_DURATION) {
				// Runde zuende
				if (!waitingForNextRound) {
					waitingForNextRound = true;

					return true;
				}
			}

			return false;
		} else {
			// PROCESS VOTES
			if (matterToVoteOn == null) {
				matterToVoteOn = city.getMattersToHoldVoteOn().pollFirst();

				onNewVote(matterToVoteOn);

				if (matterToVoteOn == null) {
					holdVote = false;
					lastTime = System.currentTimeMillis();
				}
			} else {
				if (voteFinishedTimer != -1) { // 7 Sekunden nach Vote-Ende
												// warten
					voteFinishedTimer += Gdx.graphics.getDeltaTime();

					if (voteFinishedTimer >= 7) {
						voteFinishedTimer = -1;
						matterToVoteOn = null;
					}
				}
			}

			return false;
		}
	}

	/**
	 * Has to get called to finish the current vote and go on to the next one.
	 * 
	 * @param result
	 *            the result of the vote.
	 */
	protected void finishCurrentVote(VoteResults result) {
		this.voteFinishedTimer = 0;

		processVoteResult(matterToVoteOn, result);
	}

	/**
	 * Is called by the session when a new vote is started.
	 * 
	 * @param matterToVoteOn
	 *            The new matter to vote on. Is <code>null</code> when the
	 *            voting process is over.
	 */
	protected abstract void onNewVote(VoteableMatter matterToVoteOn);

	protected void setGameSpeed(GameSpeed gameSpeed) {
		this.gameSpeed = gameSpeed;
	}

	/**
	 * This is used to process the game. It is called ten times per second if
	 * the game is running on {@linkplain GameSpeed#NORMAL normal speed}.
	 */
	public synchronized void fixedUpdate() {
		if (isRightTick(TICKS_PER_SECOND)) {
			// PROCESSING SYSTEMS
			// Character
			for (ProcessingSystem<Character> sys : characterSystems) {
				if (sys.isProcessedContinuously() || (!sys.wasProcessed())) {
					if (isRightTick(sys.getTickRate())) {
						timer.reset().start();
						for (Entry<Short, Character> e : city.getCharacters()
								.entrySet()) {
							sys.process(e.getKey(), e.getValue());
						}
						Log.info(localNetworkId == -1 ? "Server" : "Client",
								"Processed the %s-System in %d miliseconds",
								sys.getClass().getSimpleName(),
								timer.elapsed(Log.DEFAULT_TIME_UNIT));

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
						timer.reset().start();
						for (Entry<Short, Player> e : city.getPlayers()
								.entrySet()) {
							sys.process(e.getKey(), e.getValue());
						}
						Log.info(localNetworkId == -1 ? "Server" : "Client",
								"Processed the %s-System in %d miliseconds",
								sys.getClass().getSimpleName(),
								timer.elapsed(Log.DEFAULT_TIME_UNIT));

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
	 *            The relevant round end data.
	 */
	protected void processRoundEnd(RoundEndData data) {
		timer.reset().start();
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
				"Processed the RoundEnd-System in %d miliseconds",
				timer.elapsed(Log.DEFAULT_TIME_UNIT));
	}

	/**
	 * Processes the result of the given vote.
	 * 
	 * @param matterToVoteOn
	 *            The matter this vote is on.
	 * @param result
	 *            The result of the vote.
	 */
	private void processVoteResult(VoteableMatter matterToVoteOn,
			VoteResults result) {
		// ELECTION
		if (matterToVoteOn instanceof ElectionVote) {
			ElectionVote vote = ((ElectionVote) matterToVoteOn);

			// Reputation & opinion effects
			for (Entry<Short, Integer> e : result.getIndividualVotes()
					.entrySet()) {
				Character voter = city.getCharacter(e.getKey());
				for (VoteOption option : vote.getOptions()) {
					if (option.getValue() == e.getValue()) {
						voter.addOpinionModifier((short) option.getValue(), 12);
					} else {
						voter.addOpinionModifier((short) option.getValue(), -8);
					}
				}
			}

			// Actual effect
			vote.getPos().setCurrentHolder(vote.getPos().getApplicants()
					.get(result.getOverallResult()));
			vote.getPos().getApplicants().clear();

		} else
		// IMPEACHMENT
		if (matterToVoteOn instanceof ImpeachmentVote) {
			ImpeachmentVote vote = ((ImpeachmentVote) matterToVoteOn);

			// Reputation & opinion effects
			for (Entry<Short, Integer> e : result.getIndividualVotes()
					.entrySet()) {
				Character voter = city.getCharacter(e.getKey());

				if (e.getKey() == vote.getVoteCaller()) {
					voter.addOpinionModifier(vote.getCharacterToImpeach(), -18);
				}

				if (e.getValue() == ImpeachmentVote.DONT_IMPEACH_OPTION_INDEX) {
					voter.addOpinionModifier(vote.getCharacterToImpeach(), 7);
				} else {
					voter.addOpinionModifier(vote.getCharacterToImpeach(), -12);
					if (city.getCharacter(vote.getCharacterToImpeach())
							.getReputation() > 0)
						voter.setReputationModifiers(
								voter.getReputationModifiers() - 1);
				}
			}

			// Actual effect
			if (result
					.getOverallResult() == ImpeachmentVote.DONT_IMPEACH_OPTION_INDEX) {
				// Stay
			} else {
				// Remove
				vote.getPos().setCurrentHolder((short) -1);
			}
		}
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
	 * Called after a round ended to start the next round. If there are any
	 * {@linkplain City#getMattersToHoldVoteOn() matters to hold a vote on}
	 * those are done first.
	 */
	protected synchronized void startNextRound() {
		Log.debug(localNetworkId == -1 ? "Server" : "Client",
				"Runde %s zuende; %s Ticks verarbeitet", currentRound,
				currentTick);

		currentRound++;
		currentRoundTime = 0;
		lastTime = System.currentTimeMillis();
		updateTime = 0;
		currentTick = 0;

		// Reset the processing systems
		for (ProcessingSystem<Character> sys : characterSystems) {
			if (!sys.isProcessedContinuously())
				sys.setAsProcessed(false);
		}

		Log.info("Client", "Es stehen %d Tagesordnunspunkte an",
				city.getMattersToHoldVoteOn().size());

		holdVote = !city.getMattersToHoldVoteOn().isEmpty();

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
