package de.eskalon.gg.simulation;

import java.util.ArrayList;
import java.util.List;

import de.damios.guacamole.Stopwatch;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.net.packets.data.IPlayerAction;
import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.net.packets.data.PlayerActionsWrapper;
import de.eskalon.gg.net.GameClient;
import de.eskalon.gg.net.PlayerData;
import de.eskalon.gg.simulation.model.entities.Player;
import de.eskalon.gg.simulation.model.types.GameSpeed;

public class GameHandler {

	private static final Logger LOG = LoggerService
			.getLogger(GameHandler.class);

	public static final int ROUND_DURATION_IN_SECONDS = 35; // 8*60
	public static final int TICKS_PER_ROUND = ROUND_DURATION_IN_SECONDS * 1000
			/ GameSpeed.NORMAL.getTickDuration();

	private float timeAccumulator = 0;
	private int currentRound = -1;
	private int tickCountForRound = TICKS_PER_ROUND;

	private GameSimulation simulation;
	private @Inject GameClock clock;
	private List<IPlayerAction> queuedActions = new ArrayList<>();

	private GameClient client;

	private Stopwatch stopwatch = Stopwatch.createUnstarted();

	public void init(GameClient client,
			LobbyData<GameSetup, GameState, PlayerData> lobbyData) {
		simulation = new GameSimulation(lobbyData.getSessionSetup(),
				lobbyData.getPlayers(), lobbyData.getGameState(),
				client.getLocalNetworkID());
		startNextRound();
	}

	public void setClient(GameClient client) {
		this.client = client;
	}

	public boolean update(float delta) {
		timeAccumulator += delta;

		while (timeAccumulator >= simulation.getWorld().getGameSpeed()
				.getTickDuration() && tickCountForRound < TICKS_PER_ROUND) {
			timeAccumulator -= simulation.getWorld().getGameSpeed()
					.getTickDuration();

			clock.update();

			if (tickCountForRound % 15 == 0) {
				LOG.debug("[CLOCK] %02d:%02d", clock.getHour(),
						clock.getMinute());
			}

			List<PlayerActionsWrapper> actions = client
					.retrieveActionsForTurn(tickCountForRound);

			if (actions == null) {
				LOG.error(
						"[CLIENT] Actions for turn %d have not been received yet. The local simulation is lagging behind the server. ");
				return false; // Wait until we receive the actions for the
								// current turn
			}

			client.sendActions(tickCountForRound + 2, queuedActions);
			queuedActions.clear();

			simulation.onSimulationTick(tickCountForRound, actions);

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

	public Player getLocalPlayer() {
		return simulation.getWorld().getPlayers()
				.get(client.getLocalNetworkID());
	}

	public de.eskalon.gg.simulation.model.entities.Character getLocalPlayerCharacter() {
		return simulation.getWorld().getPlayers()
				.get(client.getLocalNetworkID())
				.getCurrentlyPlayedCharacter(simulation.getWorld());
	}

	public GameSimulation getSimulation() {
		return simulation;
	}

	public GameClock getClock() {
		return clock;
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
