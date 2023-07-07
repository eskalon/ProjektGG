package de.gg.game.network;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.google.gson.JsonSyntaxException;

import de.damios.guacamole.Preconditions;
import de.damios.guacamole.Stopwatch;
import de.damios.guacamole.concurrent.ThreadHandler;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.lang.Lang;
import de.gg.engine.network.BaseGameServer;
import de.gg.engine.network.ServerSettings;
import de.gg.engine.network.message.LobbyJoinRequestMessage;
import de.gg.game.asset.SimpleJSONParser;
import de.gg.game.misc.PlayerUtils;
import de.gg.game.misc.PlayerUtils.PlayerTemplate;
import de.gg.game.model.entities.Character;
import de.gg.game.model.entities.Player;
import de.gg.game.network.rmi.AuthoritativeResultListener;
import de.gg.game.network.rmi.ServersideActionHandler;
import de.gg.game.network.rmi.ServersideResultListenerStub;
import de.gg.game.session.AuthoritativeSession;
import de.gg.game.session.GameSession;
import de.gg.game.session.GameSessionSetup;
import de.gg.game.session.SavedGame;

public class GameServer extends BaseGameServer<PlayerData> {

	private static final Logger LOG = LoggerService.getLogger(GameServer.class);

	public static final String SAVES_DIR = "./saves/";

	private AuthoritativeSession session;
	private GameSessionSetup sessionSetup;

	private ObjectSpace objectSpace;
	private ServersideActionHandler actionListener;
	private ServersideResultListenerStub resultListener;
	private HashMap<Short, AuthoritativeResultListener> resultListeners = new HashMap<>();

	private List<PlayerTemplate> playerTemplates;

	/**
	 * <i>Not</i> {@code null} if the server hosts a previously saved game.
	 */
	private @Nullable SavedGame savedGame;

	/**
	 * Creates a server object with the specified settings.
	 *
	 * @param serverSettings
	 *            The server's settings, especially containing the port.
	 * @param sessionSetup
	 *            The game session's setup.
	 * @param savedGame
	 *            The saved game session to host. Can be {@code null}.
	 * @param playerStubs
	 */
	public GameServer(ServerSettings serverSettings,
			GameSessionSetup sessionSetup, @Nullable SavedGame savedGame,
			List<PlayerTemplate> playerTemplates) {
		super(serverSettings);

		Preconditions.checkNotNull(sessionSetup,
				"session setup cannot be null");
		Preconditions.checkArgument(playerTemplates.size() >= 1);

		this.sessionSetup = sessionSetup;
		this.savedGame = savedGame;
		this.playerTemplates = playerTemplates;

		/* Network Stuff */
		NetworkRegisterer.registerClasses(server.getKryo());
		ObjectSpace.registerClasses(server.getKryo());

		resultListener = new ServersideResultListenerStub(this);

		actionListener = new ServersideActionHandler(this, resultListener);
		objectSpace = new ObjectSpace();
		objectSpace.register(254, actionListener);
	}

	public void update() {
		if (session.update()) {
			LOG.info("[SERVER] Round is over");

			// Inform the clients
			resultListener.onServerReady();

			// Save automatically on the round end
			// saveGame();

			// Save the statistics
			// TODO generate & save stats for last round
			// saveStats();
		}
	}

	/**
	 * Initializes the game session when all players are ready. The
	 * {@linkplain de.gg.game.model.types type assets} have to get loaded first.
	 *
	 * @see GameSession#init(HashMap, SavedGame)
	 */
	public void initGameSession() {
		// Create session
		session = new AuthoritativeSession(sessionSetup, resultListener);
		session.init(players, null);

		actionListener.setSession(session);

		// Close Broadcast-Server
		ThreadHandler.getInstance().executeRunnable(() -> {
			stopBroadcastServer();
			LOG.info("[SERVER] Broadcast server closed");
		});

		LOG.info("[SERVER] Match initialized");
	}

	/**
	 * Starts the actual game. Has to get called after the session is
	 * {@linkplain #initGameSession() initialized}.
	 *
	 * @see AuthoritativeResultListener#onServerReady()
	 */
	public void startMatch() {
		resultListener.onServerReady();
	}

	/**
	 * Stops the server. Also takes care of saving the game.
	 */
	@Override
	public void stop() {
		// if (session != null)
		// saveGame();
		objectSpace.close();
		super.stop();
	}

	@SuppressWarnings("unused")
	private void saveGame() {
		Stopwatch timer = Stopwatch.createStarted();
		SavedGame save = session.createSaveGame();
		save.serverSetup = this.serverSettings;

		// Save the client identifiers
		for (Entry<Short, PlayerData> e : players.entrySet()) {
			save.clientIdentifiers.put(e.getKey(), e.getValue().getHostname());
		}

		// Save as file
		FileHandle savesFile = Gdx.files
				.external(SAVES_DIR + serverSettings.getGameName());

		try {
			// Rename existing save game file
			if (savesFile.exists())
				savesFile.moveTo(Gdx.files
						.external(SAVES_DIR + serverSettings.getGameName() + "_"
								+ (System.currentTimeMillis() / 1000)));

			// Save new one
			savesFile.writeString(new SimpleJSONParser().parseToJson(save),
					false);
		} catch (JsonSyntaxException e) {
			LOG.error("[SERVER] Game couldn't be saved: %s", e.getMessage());
		}

		LOG.info("[SERVER] Game was saved at '%s' (took %d ms)!",
				savesFile.path(), timer.getTime(TimeUnit.MILLISECONDS));
	}

	@Override
	protected void onPlayerDisconnected(short id) {
		if (session != null)
			resultListeners.remove(id);

		resultListener.onPlayerLeft(id);
	}

	@Override
	protected synchronized String handleLobbyJoinRequest(short id,
			LobbyJoinRequestMessage msg) {
		PlayerData p;

		if (!serverSettings.getVersion().equals(msg.getVersion())) {
			LOG.info("[SERVER] Kick: Version mismatch (%s)", msg.getVersion());
			return Lang.get("dialog.connecting_failed.version_mismatch");
		}

		if (savedGame != null) {
			short foundId = -1;
			for (Entry<Short, String> e : savedGame.clientIdentifiers
					.entrySet()) {
				if (e.getValue().equals(msg.getHostname())) {
					foundId = e.getKey();
					break;
				}
			}

			if (foundId == -1) {
				LOG.info(
						"[SERVER] Kick: Client isn't part of this loaded save game");
				return Lang.get("dialog.connecting_failed.not_in_save");
			} else {
				if (id == HOST_PLAYER_NETWORK_ID
						&& foundId != HOST_PLAYER_NETWORK_ID) { // i.e., the
																// host has
																// changed
					LOG.info(
							"[SERVER] Kick: The host of a loaded save game cannot be changed");
					// The server will get closed by the client
					return Lang
							.get("dialog.connecting_failed.cannot_change_host");
				}
				LOG.info(
						"[SERVER] Client was recognized as part of this loaded save game");
				Player oldPlayer = savedGame.world.getPlayer(foundId);
				Character oldCharacter = savedGame.world.getCharacter(
						oldPlayer.getCurrentlyPlayedCharacterId());
				p = new PlayerData(oldCharacter.getName(),
						oldCharacter.getSurname(), oldPlayer.getIcon(), -1,
						oldCharacter.isMale());
			}
		} else {
			LOG.info("[SERVER] Client %d was registered as new player", id);

			p = PlayerUtils.getRandomPlayerWithUnusedProperties(playerTemplates,
					players.values());
		}

		players.put(id, p);

		// Inform the other clients
		resultListener.onPlayerJoined(id, p);

		// Establish RMI connection (part 1)
		for (Connection con : server.getConnections()) {
			if (((short) con.getArbitraryData()) == id) {
				objectSpace.addConnection(con);
				LOG.info("[SERVER] RMI connection to client established");
			}
		}

		return null; // player will join the lobby successfully
	}

	/**
	 * @return all players currently connected to the server.
	 */
	public HashMap<Short, PlayerData> getPlayers() {
		return players;
	}

	public HashMap<Short, AuthoritativeResultListener> getResultListeners() {
		return resultListeners;
	}

	public GameSessionSetup getSessionSetup() {
		return sessionSetup;
	}

	public SavedGame getSavedGame() {
		return savedGame;
	}

	// TODO remove?
	public Collection<Connection> getConnections() {
		return server.getConnections();
	}

}
