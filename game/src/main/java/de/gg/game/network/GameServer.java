package de.gg.game.network;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.gson.JsonSyntaxException;

import de.gg.engine.log.Log;
import de.gg.engine.network.BaseGameServer;
import de.gg.engine.network.ServerSetup;
import de.gg.engine.network.message.ClientHandshakeMessage;
import de.gg.engine.network.message.ServerHandshakeMessage;
import de.gg.engine.utils.json.SimpleJSONParser;
import de.gg.game.entities.Character;
import de.gg.game.entities.Player;
import de.gg.game.network.rmi.AuthoritativeResultListener;
import de.gg.game.network.rmi.ServersideActionHandler;
import de.gg.game.network.rmi.ServersideResultListenerStub;
import de.gg.game.session.AuthoritativeSession;
import de.gg.game.session.GameSession;
import de.gg.game.session.GameSessionSetup;
import de.gg.game.session.SavedGame;
import de.gg.game.utils.PlayerUtils;
import de.gg.game.utils.PlayerUtils.PlayerStub;

public class GameServer extends BaseGameServer<LobbyPlayer> {

	public static final String SAVES_DIR = "./saves/";
	private static final Charset CHARSET = Charset.isSupported("UTF-8")
			? Charset.forName("UTF-8")
			: Charset.defaultCharset();

	private AuthoritativeSession session;
	private GameSessionSetup sessionSetup;

	private ObjectSpace objectSpace;
	private ServersideActionHandler actionListener;
	private ServersideResultListenerStub resultListener;
	private HashMap<Short, AuthoritativeResultListener> resultListeners = new HashMap<>();

	private List<PlayerStub> playerStubs;

	/**
	 * <i>Not</i> <code>null</code> if the server hosts a previously saved game.
	 */
	private @Nullable SavedGame savedGame;

	/**
	 * Creates a server object with the specified settings.
	 *
	 * @param serverSetup
	 *            The server's settings, especially containing the port.
	 * @param sessionSetup
	 *            The game session's setup.
	 * @param savedGame
	 *            The saved game session to host. Can be <code>null</code>.
	 * @param playerStubs
	 */
	public GameServer(ServerSetup serverSetup, GameSessionSetup sessionSetup,
			@Nullable SavedGame savedGame, List<PlayerStub> playerStubs) {
		super(serverSetup);
		Preconditions.checkNotNull(sessionSetup,
				"session setup cannot be null");
		Preconditions.checkArgument(
				playerStubs.size() >= serverSetup.getMaxPlayerCount(),
				"there have to be enough player stubs for the max player size");

		this.sessionSetup = sessionSetup;
		this.savedGame = savedGame;
		this.playerStubs = playerStubs;
	}

	@Override
	protected void onCreation() {
		NetworkRegisterer.registerClasses(server.getKryo());
		ObjectSpace.registerClasses(server.getKryo());

		resultListener = new ServersideResultListenerStub(this);

		actionListener = new ServersideActionHandler(this, resultListener);
		objectSpace = new ObjectSpace();
		objectSpace.register(254, actionListener);
	}

	public void update() {
		if (session.update()) {
			Log.debug("Server", "Runde zu Ende");

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
	 * {@linkplain de.gg.game.types type assets} have to get loaded first.
	 *
	 * @see GameSession#init(HashMap, SavedGame)
	 */
	public void initGameSession() {
		// Create session
		session = new AuthoritativeSession(sessionSetup, resultListener);
		session.init(players, null);

		actionListener.setSession(session);

		// Close Broadcast-Server
		(new Thread(() -> {
			stopBroadcastServer();
			Log.info("Server", "Broadcast-Server geschlossen");
		})).start();

		Log.info("Server", "Match initialisiert");
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
		save.serverSetup = this.serverSetup;

		// Save the client identifiers
		for (Entry<Short, LobbyPlayer> e : players.entrySet()) {
			save.clientIdentifiers.put(e.getKey(), e.getValue().getHostname());
		}

		// Save as file
		File savesFile = new File(SAVES_DIR + serverSetup.getGameName());

		try {
			// Rename existing save game file
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
	protected void onPlayerDisconnected(Connection con, short id) {
		if (session != null)
			resultListeners.remove(id);

		resultListener.onPlayerLeft(id);
	}

	@Override
	protected synchronized void onClientHandshake(Connection con,
			ClientHandshakeMessage msg) {
		Short id = connections.get(con);

		LobbyPlayer p;

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
				Log.info("Server",
						"Kick: Client ist kein Teil dieser geladenen Partie");
				con.sendTCP(new ServerHandshakeMessage(
						"Der Spieler ist kein Teil dieser geladenen Partie!"));
				con.close();
				return;
			} else {
				if ((id == HOST_PLAYER_NETWORK_ID
						&& foundId != HOST_PLAYER_NETWORK_ID)
						|| (foundId == HOST_PLAYER_NETWORK_ID
								&& id != HOST_PLAYER_NETWORK_ID)) {
					// Host has hanged changed
					Log.info("Server",
							"Kick: Der Host einer geladenen Partie kann nicht verändert werden");
					con.sendTCP(new ServerHandshakeMessage(
							"Der Host einer geladenen Partie kann nicht verändert werden!"));
					con.close();
					// Server gets closed if need be
					return;
				}
				Log.info("Server",
						"Client als Teil der geladenen Partie erkannt");
				Player oldPlayer = savedGame.world.getPlayer(foundId);
				Character oldCharacter = savedGame.world.getCharacter(
						oldPlayer.getCurrentlyPlayedCharacterId());
				p = new LobbyPlayer(oldCharacter.getName(),
						oldCharacter.getSurname(), oldPlayer.getIcon(), -1,
						oldCharacter.isMale());
			}
		} else {
			Log.info("Server", "Client %d als neuen Spieler registriert", id);

			p = PlayerUtils.getRandomPlayerWithUnusedProperties(playerStubs,
					players.values());
		}

		players.put(id, p);

		// Inform the other clients
		resultListener.onPlayerJoined(id, p);

		// Establish RMI connection (part 1)
		objectSpace.addConnection(con);
		Log.info("Server", "RMI-Netzwerkverbindung zu Client eingerichtet");

		// Perform the handshake
		con.sendTCP(new ServerHandshakeMessage(id));
	}

	/**
	 * @return all players currently connected to the server.
	 */
	public HashMap<Short, LobbyPlayer> getPlayers() {
		return players;
	}

	public HashMap<Short, AuthoritativeResultListener> getResultListeners() {
		return resultListeners;
	}

	public HashMap<Connection, Short> getConnections() {
		return connections;
	}

	public GameSessionSetup getSessionSetup() {
		return sessionSetup;
	}

	public SavedGame getSavedGame() {
		return savedGame;
	}

}
