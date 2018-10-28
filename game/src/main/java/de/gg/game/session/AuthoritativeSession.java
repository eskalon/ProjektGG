package de.gg.game.session;

import java.util.HashMap;

import javax.annotation.Nullable;

import de.gg.engine.log.Log;
import de.gg.game.ai.CharacterBehaviour;
import de.gg.game.data.vote.VoteResults;
import de.gg.game.entities.Character;
import de.gg.game.entities.Player;
import de.gg.game.network.LobbyPlayer;
import de.gg.game.network.rmi.AuthoritativeResultListener;
import de.gg.game.network.rmi.ServersideResultListenerStub;
import de.gg.game.systems.ProcessingSystem;
import de.gg.game.systems.server.FirstCharacterEventWaveServerSystem;
import de.gg.game.systems.server.FirstPlayerEventWaveServerSystem;
import de.gg.game.systems.server.IllnessDamageSystem;
import de.gg.game.systems.server.NpcActionSystem;
import de.gg.game.systems.server.NpcActionSystem2;
import de.gg.game.systems.server.ServerProcessingSystem;
import de.gg.game.votes.VoteUtils;
import de.gg.game.votes.VoteableMatter;

/**
 * This class takes care of simulating the game session on the server side.
 */
public class AuthoritativeSession extends GameSession {

	// private HashMap<Short, AuthoritativeResultListener> resultListeners;
	private AuthoritativeResultListener clientResultListeners;

	/**
	 * A hashmap of the individual votes for the currently held vote. The
	 * characters's ID is the key and the vote option is the value.
	 */
	private HashMap<Short, Integer> individualVotes = new HashMap<>();

	/**
	 * Creates a new multiplayer session.
	 *
	 * @param sessionSetup
	 *            the settings of the game session.
	 * @param clientResultListeners
	 */
	public AuthoritativeSession(GameSessionSetup sessionSetup,
			ServersideResultListenerStub clientResultListeners) {
		super(sessionSetup, (short) -1);

		this.clientResultListeners = clientResultListeners;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(HashMap<Short, LobbyPlayer> players,
			@Nullable SavedGame savedGame) {
		for (LobbyPlayer player : players.values()) {
			player.setReady(false);
		}

		super.init(players, savedGame);

		// Setup the server processing systems
		ServerProcessingSystem<Character> s;
		s = new FirstCharacterEventWaveServerSystem(clientResultListeners);
		s.init(world, sessionSetup.getSeed());
		this.characterSystems.add(s);

		s = new NpcActionSystem(clientResultListeners);
		s.init(world, sessionSetup.getSeed());
		this.characterSystems.add(s);

		s = new NpcActionSystem2(clientResultListeners);
		s.init(world, sessionSetup.getSeed());
		this.characterSystems.add(s);

		ServerProcessingSystem<Player> s2;

		s2 = new FirstPlayerEventWaveServerSystem(clientResultListeners);
		s2.init(world, sessionSetup.getSeed());
		this.playerSystems.add(s2);

		s2 = new IllnessDamageSystem(clientResultListeners);
		s2.init(world, sessionSetup.getSeed());
		this.playerSystems.add(s2);

		// Load the systems states
		if (savedGame != null) {
			for (ProcessingSystem<Character> c : characterSystems) {
				((ServerProcessingSystem<Character>) c)
						.loadSavedState(savedGame.processingSystemStates
								.get(c.getClass().getSimpleName()));
			}
			for (ProcessingSystem<Player> p : playerSystems) {
				((ServerProcessingSystem<Player>) p)
						.loadSavedState(savedGame.processingSystemStates
								.get(p.getClass().getSimpleName()));
			}
		}
	}

	@Override
	protected void onNewVote(VoteableMatter matterToVoteOn) {
		individualVotes.clear();

		if (matterToVoteOn != null) {
			// AI vote
			for (short charId : matterToVoteOn.getVoters()) {
				boolean isPlayer = false;

				for (Player p : world.getPlayers().values()) {
					if (p.getCurrentlyPlayedCharacterId() == charId) {
						isPlayer = true;
						break;
					}
				}

				if (!isPlayer) {
					individualVotes.put(charId, CharacterBehaviour
							.getVoteOption(charId, matterToVoteOn, this));
				}
			}

			// Check if all votes were made
			if (individualVotes.size() == matterToVoteOn.getVoters().size()) {
				VoteResults result = new VoteResults(
						VoteUtils.getVoteResult(matterToVoteOn, individualVotes,
								sessionSetup.getSeed()),
						individualVotes);
				finishCurrentVote(result);
				clientResultListeners.onVoteFinished(result);
			}
		}
	}

	public SavedGame createSaveGame() {
		Log.info("Server", "Spiel speichern...");

		SavedGame save = new SavedGame();
		save.world = this.world;
		save.gameSessionSetup = sessionSetup;
		save.currentRound = getCurrentRound();
		save.lastProcessedTick = getTickCount();

		// Save the systems states
		for (ProcessingSystem<Character> c : characterSystems) {
			save.processingSystemStates.put(c.getClass().getSimpleName(),
					((ServerProcessingSystem<Character>) c).getSaveState());
		}
		for (ProcessingSystem<Player> p : playerSystems) {
			save.processingSystemStates.put(p.getClass().getSimpleName(),
					((ServerProcessingSystem<Player>) p).getSaveState());
		}

		return save;
	}

	/**
	 * A hashmap containing each individual vote. The characters's ID is the key
	 * and the vote option is the value.
	 *
	 * @return the individual votes.
	 */
	public HashMap<Short, Integer> getIndividualVotes() {
		return individualVotes;
	}

}
