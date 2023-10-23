package de.eskalon.gg.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;

import de.damios.guacamole.Stopwatch;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.net.packets.data.IPlayerAction;
import de.eskalon.commons.net.packets.data.PlayerActionsWrapper;
import de.eskalon.gg.net.PlayerData;
import de.eskalon.gg.simulation.actions.GameSpeedChangeAction;
import de.eskalon.gg.simulation.actions.handlers.GameSpeedChangeActionHandler;
import de.eskalon.gg.simulation.actions.handlers.IPlayerActionHandler;
import de.eskalon.gg.simulation.ai.CharacterBehaviour;
import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.entities.Character;
import de.eskalon.gg.simulation.model.entities.Player;
import de.eskalon.gg.simulation.model.votes.Ballot;
import de.eskalon.gg.simulation.model.votes.BallotUtils;
import de.eskalon.gg.simulation.systems.IProcessingSystem;
import de.eskalon.gg.simulation.systems.character.FirstEventWaveCharacterSystem;
import de.eskalon.gg.simulation.systems.character.RoundStartCharacterSystem;
import de.eskalon.gg.simulation.systems.player.FirstEventWavePlayerSystem;
import de.eskalon.gg.simulation.systems.player.PlayerTickSystem;
import de.eskalon.gg.simulation.systems.player.RoundStartPlayerSystem;

public class GameSimulation {

	private static final Logger LOG = LoggerService
			.getLogger(GameSimulation.class);
	private String logTag;

	protected World world;
	protected ArrayList<IProcessingSystem<Character>> characterSystems = new ArrayList<>();
	protected ArrayList<IProcessingSystem<Player>> playerSystems = new ArrayList<>();

	protected HashMap<Class<?>, IPlayerActionHandler<?>> actionHandlers = new HashMap<>();

	private Stopwatch logTimer = Stopwatch.createUnstarted();

	public GameSimulation(GameSetup setup, IntMap<PlayerData> players,
			@Nullable GameState savedGameState, short localPlayerId) {
		logTag = localPlayerId == -1 ? "[SERVER]" : "[CLIENT]";

		/* Generate world */
		if (savedGameState == null) {
			this.world = new World();
			this.world.generate(setup, players);
		} else {
			//@formatter:off
//			this.world = savedGame.world;
//			this.currentRound = savedGame.currentRound;
//			// Session- & server setup get set in the constructors
//
//			// Switch player IDs when loading game
//			if (savedGame != null) {
//				for (Entry<Short, PlayerData> newE : players.entrySet()) {
//					for (Entry<Short, String> oldE : savedGame.clientIdentifiers
//							.entrySet()) {
//						if (newE.getValue().getHostname()
//								.equals(oldE.getValue())) {
//							// Change all mentions of the saved player id to the
//							// new one
//							// Use negative numbers so there are no collisions
//							Player p = world.getPlayers().remove(oldE.getKey());
//							world.getPlayers().put((short) -newE.getKey(), p);
//						}
//					}
//				}
//				// Revert the IDs back to positive numbers
//				for (short s : savedGame.world.getPlayers().keySet()) {
//					Player p = world.getPlayers().remove(s);
//					world.getPlayers().put((short) -s, p);
//				}
//			}
//
			//@formatter:on
		}

		/* Initialize systems */
		characterSystems.add(new FirstEventWaveCharacterSystem(world));
		characterSystems.add(new RoundStartCharacterSystem());

		playerSystems.add(new FirstEventWavePlayerSystem(world, localPlayerId));
		playerSystems.add(new PlayerTickSystem(world));
		playerSystems.add(new RoundStartPlayerSystem(world, localPlayerId));

		//@formatter:off
//		// Load the systems states
//		if (savedGame != null) {
//			for (ProcessingSystem<Character> c : characterSystems) {
//				((ServerProcessingSystem<Character>) c)
//						.loadSavedState(savedGame.processingSystemStates
//								.get(c.getClass().getSimpleName()));
//			}
//			for (ProcessingSystem<Player> p : playerSystems) {
//				((ServerProcessingSystem<Player>) p)
//						.loadSavedState(savedGame.processingSystemStates
//								.get(p.getClass().getSimpleName()));
//			}
//		}
		//@formatter:on

		/* Initialize action handlers */
		actionHandlers.put(GameSpeedChangeAction.class,
				new GameSpeedChangeActionHandler());
	}

	public World getWorld() {
		return world;
	}

	public void onSimulationTick(int tick, List<PlayerActionsWrapper> actions) {
		logTimer.reset().start();

		/* Execute received commands */
		for (PlayerActionsWrapper actionWrapper : actions) {
			for (IPlayerAction action : actionWrapper.getActions()) {
				((IPlayerActionHandler) actionHandlers.get(action.getClass()))
						.handle(world, actionWrapper.getPlayerId(), action);
			}
		}

		/* Process stuff for this tick */
		// Character
		for (Entry<Character> e : world.getCharacters().entries()) {
			for (IProcessingSystem<Character> s : characterSystems) {
				if (s.doProcess(tick))
					s.process((short) e.key, e.value);
			}
		}

		// Player
		for (Entry<Player> e : world.getPlayers().entries()) {
			for (IProcessingSystem<Player> s : playerSystems) {
				if (s.doProcess(tick))
					s.process((short) e.key, e.value);
			}
		}

		long time = logTimer.getTime(TimeUnit.MILLISECONDS);
		if (time > 1)
			LOG.info("%s Simulation tick %d processed in %d ms", logTag, tick,
					time);
	}

	public void onRoundStart() {
		// not needed?
	}

	public int processVotes(Ballot matterToVoteOn,
			HashMap<Short, Integer> receivedVotes) {
		// Take care of the votes which weren't cast
		if (receivedVotes.size() != matterToVoteOn.getVoters().size()) {
			for (short charId : matterToVoteOn.getVoters()) {
				if (!receivedVotes.containsKey(charId)) {
					receivedVotes.put(charId, CharacterBehaviour
							.getVoteOption(charId, matterToVoteOn, world));
				}
			}
		}

		// Calculate results & process them
		int result = BallotUtils.getBallotResult(matterToVoteOn, receivedVotes,
				world.getSeed());
		matterToVoteOn.processVoteResult(receivedVotes, result, world);

		return result;
	}

	//@formatter:off
//	public SavedGame createSaveGame() {
//		LOG.info("%s Spiel speichern...", logTag);
//
//		SavedGame save = new SavedGame();
//		save.world = this.world;
//		save.gameSessionSetup = sessionSetup;
//		save.currentRound = getCurrentRound();
//		save.lastProcessedTick = getTickCount();
//
//		// Save the systems states
//		for (ProcessingSystem<Character> c : characterSystems) {
//			save.processingSystemStates.put(c.getClass().getSimpleName(),
//					((ServerProcessingSystem<Character>) c).getSaveState());
//		}
//		for (ProcessingSystem<Player> p : playerSystems) {
//			save.processingSystemStates.put(p.getClass().getSimpleName(),
//					((ServerProcessingSystem<Player>) p).getSaveState());
//		}
//
//		return save;
//	}
	//@formatter:on

}
