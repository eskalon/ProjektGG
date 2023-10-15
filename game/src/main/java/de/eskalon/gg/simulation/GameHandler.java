package de.eskalon.gg.simulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.damios.guacamole.Stopwatch;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.net.packets.data.IPlayerAction;
import de.eskalon.gg.simulation.ai.CharacterBehaviour;
import de.eskalon.gg.simulation.model.entities.Player;
import de.eskalon.gg.simulation.model.types.GameSpeed;
import de.eskalon.gg.simulation.model.votes.Ballot;

public class GameHandler {

	private static final Logger LOG = LoggerService
			.getLogger(GameHandler.class);

	public static final int ROUND_DURATION_IN_SECONDS = 35; // 8*60
	private static final int TICKS_PER_SECOND = 10;
	public static final int TICKS_PER_ROUND = ROUND_DURATION_IN_SECONDS
			* TICKS_PER_SECOND;
	private static final int TICK_DURATION = 1000
			* GameSpeed.NORMAL.getDeltaTimeMultiplied() / TICKS_PER_SECOND;

	private float timeAccumulator = 0;
	private int currentRound = -1;
	private int tickCountForRound = TICKS_PER_ROUND;
	// private GameSpeed gameSpeed = GameSpeed.NORMAL;

	private SlaveSimulation simulation;
	private @Inject GameClock clock;
	private List<IPlayerAction> queuedActions = new ArrayList<>();
	private Queue<Ballot> mattersToVoteOn = new LinkedList<>();
	private short localPlayerId;

	private Stopwatch stopwatch = Stopwatch.createUnstarted();

	public GameHandler() {
		simulation = new SlaveSimulation(null, null); // TODO
	}

	public boolean update(float delta) {
		timeAccumulator += delta;

		while (timeAccumulator >= TICK_DURATION
				&& tickCountForRound < TICKS_PER_ROUND) {
			timeAccumulator -= TICK_DURATION;

			clock.update();

			if (tickCountForRound % 15 == 0) {
				LOG.debug("[CLOCK] %02d:%02d", clock.getHour(),
						clock.getMinute());
			}

			// TODO GameClient#sendCommandsToServer() for currentTick + 2

			simulation.onSimulationTick(tickCountForRound); // TODO poll
			// client.getActionsForTurn()

			if (tickCountForRound == TICKS_PER_ROUND) {
				return true;
			}

			tickCountForRound++;
		}

		if (tickCountForRound < TICKS_PER_ROUND)
			updateVisualStuff(delta);

		return false;
	}

	public void startNextRound() {
		currentRound++;
		simulation.onRoundStart();
		timeAccumulator = 0;
		tickCountForRound = 0;
		clock.resetClock();
		clock.setRound(currentRound);

		LOG.debug("[CLIENT] Round %d was started", currentRound);
	}

	protected void updateVisualStuff(float delta) {
		// e.g. entity positions, particle effects, etc.; interpolate stuff
	}

	public void executeAction(IPlayerAction action) {
		queuedActions.add(action);
	}

	public void dispose() {
		//
	}

	public void setLocalPlayerId(short id) {
		this.localPlayerId = id;
	}

	public Player getLocalPlayer() {
		return simulation.getWorld().getPlayers().get(localPlayerId);
	}

	public de.eskalon.gg.simulation.model.entities.Character getLocalPlayerCharacter() {
		return simulation.getWorld().getPlayers().get(localPlayerId)
				.getCurrentlyPlayedCharacter(simulation.getWorld());
	}

	public SlaveSimulation getSimulation() {
		return simulation;
	}

	public GameClock getClock() {
		return clock;
	}

	/**
	 * @return matters on which a vote is held on after this round.
	 */
	public Queue<Ballot> getMattersToHoldVoteOn() {
		return mattersToVoteOn;
	}

	/**
	 * @param otherCharacterId
	 * @return the opinion another character has about the player.
	 */
	public int getOpinionOfOtherCharacter(short otherCharacterId) {
		return CharacterBehaviour.getOpinionOfAnotherCharacter(
				getLocalPlayer().getCurrentlyPlayedCharacterId(),
				otherCharacterId, session);
	}

	// @formatter:off
//	public void saveGame() {
//		//TODO return SavedGame, do file stuff outside of this class!
//		Stopwatch timer = Stopwatch.createStarted();
//		SavedGame save = session.createSaveGame();
//		save.serverSetup = this.serverSettings;
//
//		// Save the client identifiers
//		for (Entry<Short, PlayerData> e : players.entrySet()) {
//			save.clientIdentifiers.put(e.getKey(), e.getValue().getHostname());
//		}
//
//		// Save as file
//		FileHandle savesFile = Gdx.files
//				.external(SAVES_DIR + serverSettings.getGameName());
//
//		try {
//			// Rename existing save game file
//			if (savesFile.exists())
//				savesFile.moveTo(Gdx.files
//						.external(SAVES_DIR + serverSettings.getGameName() + "_"
//								+ (System.currentTimeMillis() / 1000)));
//
//			// Save new one
//			savesFile.writeString(new SimpleJSONParser().parseToJson(save),
//					false);
//		} catch (JsonSyntaxException e) {
//			LOG.error("[SERVER] Game couldn't be saved: %s",
//					Exceptions.getStackTraceAsString(e));
//		}
//
//		LOG.info("[SERVER] Game was saved at '%s' (took %d ms)!",
//				savesFile.path(), timer.getTime(TimeUnit.MILLISECONDS));
//	}
	// @formatter:on

}
