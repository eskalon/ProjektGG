package de.gg.game;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonSyntaxException;

import de.gg.game.ai.CharacterBehaviour;
import de.gg.game.data.vote.VoteResults;
import de.gg.game.entities.Character;
import de.gg.game.entities.Player;
import de.gg.game.entities.Position;
import de.gg.game.systems.ProcessingSystem;
import de.gg.game.systems.server.FirstCharacterEventWaveServerSystem;
import de.gg.game.systems.server.FirstPlayerEventWaveServerSystem;
import de.gg.game.systems.server.IllnessDamageSystem;
import de.gg.game.systems.server.NpcActionSystem;
import de.gg.game.systems.server.NpcActionSystem2;
import de.gg.game.systems.server.ServerProcessingSystem;
import de.gg.game.types.GameSpeed;
import de.gg.game.types.PositionType;
import de.gg.game.votes.ElectionVote;
import de.gg.game.votes.ImpeachmentVote;
import de.gg.game.votes.VoteableMatter;
import de.gg.network.GameServer;
import de.gg.network.LobbyPlayer;
import de.gg.network.ServerSetup;
import de.gg.network.rmi.AuthoritativeResultListener;
import de.gg.network.rmi.ServerAuthoritativResultListenerStub;
import de.gg.network.rmi.SlaveActionListener;
import de.gg.utils.CollectionUtils;
import de.gg.utils.PlayerUtils;
import de.gg.utils.json.SimpleJSONParser;
import de.gg.utils.log.Log;

/**
 * This class takes care of simulating the game session on the server side and
 * implements the {@linkplain SlaveActionHandler interface} used in the RMI for
 * the client.
 */
public class AuthoritativeSession extends GameSession
		implements SlaveActionListener {

	public static final String SAVES_DIR = "./saves/";
	private static final Charset CHARSET = Charset.isSupported("UTF-8")
			? Charset.forName("UTF-8")
			: Charset.defaultCharset();

	private HashMap<Short, AuthoritativeResultListener> resultListeners;
	private AuthoritativeResultListener resultListenerStub;

	private ServerSetup serverSetup;

	/**
	 * The (temporary) results of the currently held vote.
	 */
	private VoteResults voteResults;

	/**
	 * Creates a new multiplayer session.
	 *
	 * @param sessionSetup
	 *            the settings of the game session.
	 * @param players
	 *            a hashmap containing the players.
	 * @param localNetworkId
	 *            the local player's network id.
	 */
	public AuthoritativeSession(GameSessionSetup sessionSetup,
			ServerSetup serverSetup, HashMap<Short, LobbyPlayer> players) {
		super(sessionSetup, players, (short) -1);

		this.resultListenerStub = new ServerAuthoritativResultListenerStub(
				this);
		this.serverSetup = serverSetup;
	}

	public void setResultListeners(
			HashMap<Short, AuthoritativeResultListener> resultListeners) {
		this.resultListeners = resultListeners;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(@Nullable SavedGame savedGame) {
		for (LobbyPlayer player : players.values()) {
			player.setReady(false);
		}

		super.init(savedGame);

		// Setup the server processing systems
		ServerProcessingSystem<Character> s;
		s = new FirstCharacterEventWaveServerSystem(resultListenerStub);
		s.init(world, getGameSeed());
		this.characterSystems.add(s);

		s = new NpcActionSystem(resultListenerStub);
		s.init(world, getGameSeed());
		this.characterSystems.add(s);

		s = new NpcActionSystem2(resultListenerStub);
		s.init(world, getGameSeed());
		this.characterSystems.add(s);

		ServerProcessingSystem<Player> s2;

		s2 = new FirstPlayerEventWaveServerSystem(resultListenerStub);
		s2.init(world, getGameSeed());
		this.playerSystems.add(s2);

		s2 = new IllnessDamageSystem(resultListenerStub);
		s2.init(world, getGameSeed());
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fixedUpdate() {
		super.fixedUpdate();

		if (isRightTick(15)) {
			// Update production progress
		}
	}

	public void onRoundEnd() {
		Log.debug("Server", "Runde zu Ende");

		// Inform the clients
		resultListenerStub.onServerReady();

		// Save automatically on the round end
		// saveGame();

		// Save the statistics
		saveStats();
	}

	@Override
	protected void onNewVote(VoteableMatter matterToVoteOn) {
		voteResults = new VoteResults();

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
					voteResults.getIndividualVotes().put(charId,
							CharacterBehaviour.getVoteOption(charId,
									matterToVoteOn, this));
				}
			}

			// Check if all votes were made
			if (voteResults.getIndividualVotes().size() == matterToVoteOn
					.getVoters().size()) {
				sendVoteResult();
			}
		}
	}

	@Override
	public void onVoteCast(int chosenOption, short clientId) {
		voteResults.getIndividualVotes().put(
				world.getPlayer(clientId).getCurrentlyPlayedCharacterId(),
				chosenOption);

		// Check if all votes were made
		if (voteResults.getIndividualVotes().size() == matterToVoteOn
				.getVoters().size()) {
			sendVoteResult();
		}
	}

	/**
	 * Sends the vote result to the clients.
	 */
	private void sendVoteResult() {
		try {
			// Find the most common vote option
			Map<Integer, Integer> resultMap = new HashMap<>();
			for (int i : voteResults.getIndividualVotes().values()) {
				Integer count = resultMap.get(i);
				resultMap.put(i, count != null ? count + 1 : 1);
			}

			resultMap = CollectionUtils.sortByValue(resultMap);

			@SuppressWarnings("unchecked")
			Entry<Integer, Integer>[] entries = resultMap.entrySet()
					.toArray(new Entry[0]);
			// A tie
			if (entries.length > 1
					&& entries[0].getValue() == entries[1].getValue()) {
				List<Integer> resultOptions = new ArrayList<>();

				// Collect all tied options
				for (Entry<Integer, Integer> entry : entries) {
					if (entry.getValue() == entries[0].getValue()) {
						resultOptions.add(entry.getKey());
					}
				}
				if (!(matterToVoteOn instanceof ElectionVote)
						&& matterToVoteOn.getOptions().size() == 2) // the
																	// majority
																	// is needed
					voteResults.setOverallResult(-1);
				else // Random option wins
					voteResults.setOverallResult(
							CollectionUtils.getRandomElementInList(
									resultOptions, new Random(getGameSeed())));

			} else {
				// One result
				voteResults.setOverallResult(entries[0].getKey());
			}

			this.finishCurrentVote(voteResults);

			resultListenerStub.onVoteFinished(voteResults);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveStats() {
		// TODO generate & save stats for last round
	}

	public void saveGame() {
		Log.info("Server", "Spiel speichern...");
		Stopwatch timer = Stopwatch.createStarted();

		SavedGame save = new SavedGame();
		save.world = this.world;
		save.serverSetup = this.serverSetup;
		GameSessionSetup sessionSetup = new GameSessionSetup(getDifficulty(),
				getMap(), getGameSeed());
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

		// Client identifiers
		for (Entry<Short, LobbyPlayer> e : players.entrySet()) {
			save.clientIdentifiers.put(e.getKey(), e.getValue().getHostname());
		}

		// Save in file
		File savesFile = new File(SAVES_DIR + serverSetup.getGameName());

		try {
			// Rename old file
			if (savesFile.exists())
				FileUtils.copyFile(savesFile,
						new File(SAVES_DIR + serverSetup.getGameName() + "_"
								+ (System.currentTimeMillis() / 1000)));

			// Save new one
			SimpleJSONParser saveGameParser = new SimpleJSONParser();
			FileUtils.writeStringToFile(savesFile,
					saveGameParser.parseToJson(save), CHARSET, false);
		} catch (JsonSyntaxException | IOException e) {
			Log.error("Server", "Spiel konnte nicht gespeichert werden: %s",
					e.getMessage());
		}

		Log.info("Server", "Spiel gespeichert als '%s' in %d miliseconds!",
				savesFile.getAbsolutePath(),
				timer.elapsed(Log.DEFAULT_TIME_UNIT));
	}

	@Override
	public boolean readyUp(short networkId) {
		if (players.get(networkId).isReady()) {
			return false;
		}

		players.get(networkId).setReady(true);

		Log.info("Server", "Spieler %d ist für nächste Runde bereit",
				networkId);

		if (PlayerUtils.areAllPlayersReady(players.values()))
			startNextRoundForEveryone();

		return true;
	}

	public synchronized void startNextRoundForEveryone() {
		Log.info("Server", "Alle Spieler sind für die Runde bereit");

		for (LobbyPlayer player : players.values()) {
			player.setReady(false);
		}

		resultListenerStub.onAllPlayersReadied();

		startNextRound();
	}

	/**
	 * @return the result listener stub used to distribute events to all
	 *         clients.
	 */
	public AuthoritativeResultListener getResultListenerStub() {
		return resultListenerStub;
	}

	/**
	 * @return a hashmap of all registered result listeners.
	 */
	public HashMap<Short, AuthoritativeResultListener> getResultListeners() {
		return resultListeners;
	}

	@Override
	public void increaseGameSpeed(short clientId) {
		if (!serverSetup.isHostOnlyCommands()
				|| clientId == GameServer.HOST_PLAYER_NETWORK_ID) {
			int index = gameSpeed.ordinal() + 1;

			gameSpeed = GameSpeed
					.values()[index >= GameSpeed.values().length ? 0 : index];
			resultListenerStub.setGameSpeed(gameSpeed.ordinal());
		}
	}

	@Override
	public void decreaseGameSpeed(short clientId) {
		if (!serverSetup.isHostOnlyCommands()
				|| clientId == GameServer.HOST_PLAYER_NETWORK_ID) {
			int index = gameSpeed.ordinal() - 1;

			gameSpeed = GameSpeed.values()[index < 0
					? GameSpeed.values().length - 1
					: index];
			resultListenerStub.setGameSpeed(gameSpeed.ordinal());
		}
	}

	@Override
	public boolean onImpeachmentVoteArranged(short targetCharacterId,
			short clientId) {
		PositionType t = world.getCharacters().get(targetCharacterId)
				.getPosition();

		if (t != null) {
			// TODO überprüfen, ob nicht bereits ein anderer einen Vote
			// initiiert hat

			world.getMattersToHoldVoteOn().add(new ImpeachmentVote(world, t,
					world.getPlayer(clientId).getCurrentlyPlayedCharacterId()));

			resultListenerStub.onImpeachmentVoteArranged(targetCharacterId,
					world.getPlayer(clientId).getCurrentlyPlayedCharacterId());
			return true;
		}

		return false;
	}

	@Override
	public boolean onAppliedForPosition(PositionType t, short clientId) {
		Position pos = world.getPositions().get(t);

		if (pos.getCurrentHolder() == (short) -1
				&& pos.getApplicants().size() < 4) {
			pos.getApplicants().add(
					world.getPlayer(clientId).getCurrentlyPlayedCharacterId());

			resultListenerStub.onAppliedForPosition(clientId, t);

			return true;
		}

		return false;
	}

}
