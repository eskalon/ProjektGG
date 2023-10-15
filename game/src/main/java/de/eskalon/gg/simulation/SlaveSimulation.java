package de.eskalon.gg.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import de.damios.guacamole.Stopwatch;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.gg.net.PlayerData;
import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.entities.Character;
import de.eskalon.gg.simulation.model.entities.Player;
import de.eskalon.gg.simulation.systems.IProcessingSystem;
import de.eskalon.gg.simulation.systems.character.FirstEventWaveCharacterSystem;
import de.eskalon.gg.simulation.systems.character.RoundStartCharacterSystem;
import de.eskalon.gg.simulation.systems.player.FirstEventWavePlayerSystem;
import de.eskalon.gg.simulation.systems.player.PlayerTickSystem;
import de.eskalon.gg.simulation.systems.player.RoundStartPlayerSystem;

public class SlaveSimulation {

	private static final Logger LOG = LoggerService
			.getLogger(SlaveSimulation.class);

	protected World world;
	protected ArrayList<IProcessingSystem<Character>> characterSystems = new ArrayList<>();
	protected ArrayList<IProcessingSystem<Player>> playerSystems = new ArrayList<>();

	private Stopwatch logTimer = Stopwatch.createUnstarted();

	public SlaveSimulation(GameSetup setup, HashMap<Short, PlayerData> players,
			@Nullable SavedGame savedGame, short localPlayerId) {
		/* Generate world */
		if (savedGame == null) {
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
//			// TODO in den Konstruktoren die Setups setzen
//			// TODO in der Lobby das Setup disablen
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
	}

	public World getWorld() {
		return world;
	}

	public void onSimulationTick(int tick) {
		logTimer.reset().start();

		/* Execute received commands */
		// TODO poll client.getActionsForTurn()

		/* Process stuff for this tick */
		// Character
		for (Entry<Short, Character> e : world.getCharacters().entrySet()) {
			for (IProcessingSystem<Character> s : characterSystems) {
				if (s.doProcess(tick))
					s.process(e.getKey(), e.getValue());
			}
		}

		// Player
		for (Entry<Short, Player> e : world.getPlayers().entrySet()) {
			for (IProcessingSystem<Player> s : playerSystems) {
				if (s.doProcess(tick))
					s.process(e.getKey(), e.getValue());
			}
		}

		long time = logTimer.getTime(TimeUnit.MILLISECONDS);
		if (time > 1)
			LOG.info("Simulation tick %d processed in %d ms", tick, time);
	}

	public void onRoundStart() {
		// TODO fake the actions for the first two ticks!

	}

	//@formatter:off
//	public SavedGame createSaveGame() {
//		LOG.info("[SERVER] Spiel speichern...");
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
