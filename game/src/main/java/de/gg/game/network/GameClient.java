package de.gg.game.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.gg.engine.log.Log;
import de.gg.engine.network.BaseGameClient;
import de.gg.game.ai.CharacterBehaviour;
import de.gg.game.entities.Player;
import de.gg.game.events.DisconnectionEvent;
import de.gg.game.events.GameDataReceivedEvent;
import de.gg.game.events.NewNotificationEvent;
import de.gg.game.events.NotificationCreationEvent;
import de.gg.game.events.RoundEndEvent;
import de.gg.game.network.rmi.ClientsideActionHandler;
import de.gg.game.network.rmi.ClientsideResultListener;
import de.gg.game.network.rmi.SlaveActionListener;
import de.gg.game.session.GameSession;
import de.gg.game.session.GameSessionSetup;
import de.gg.game.session.SavedGame;
import de.gg.game.session.SlaveSession;
import de.gg.game.ui.data.NotificationData;

/**
 * This class takes care of handling the networking part for the client. It
 * holds an instance of the used kryonet {@linkplain #client client}, the
 * {@linkplain #session client's game simulation} and the
 * {@linkplain #actionHandler action handler} used for relaying all user actions
 * to the server.
 * <p>
 * Following are all methods concerning the state of the client:
 * <ul>
 * <li>
 * {@link #connect(de.gg.engine.network.IClientConnectCallback, String, String, int)}:
 * Connects the client to the server asynchronously. After it is finished the
 * given listener is called. After that the client requests the lobby data. When
 * he receives it a {@link GameDataReceivedEvent} is posted.</li>
 * <li>{@link #initGameSession()}: Initializes the client's game session. Has to
 * get called after the client is connected and before the session is
 * {@linkplain GameSession#update() updated}.</li>
 * <li>{@link #update()}: Has to get called continually to update the client's
 * {@linkplain SlaveSession game session}.</li>
 * <li>{@link #disconnect()}: Disconnects the client synchronously.</li>
 * </ul>
 */
public class GameClient extends BaseGameClient {
	private EventBus eventBus;

	private ClientsideActionHandler actionHandler;
	private ClientsideResultListener resultListener;
	private ObjectSpace objectSpace;

	private List<NotificationData> notifications = new ArrayList<>();

	private SlaveSession session;

	/**
	 * Creates a game client.
	 *
	 * @param eventBus
	 *            the event bus used by the client.
	 */
	public GameClient(EventBus eventBus) {
		Preconditions.checkNotNull(eventBus, "eventBus cannot be null.");

		this.eventBus = eventBus;
		this.eventBus.register(this);

		this.resultListener = new ClientsideResultListener(eventBus);
	}

	@Override
	protected void onCreation() {
		NetworkRegisterer.registerClasses(client.getKryo());
	}

	@Override
	protected void onDisconnection() {
		eventBus.post(new DisconnectionEvent());
	}

	@Override
	protected void onSuccessfulHandshake() {
		// Establish RMI
		ObjectSpace.registerClasses(client.getKryo());
		objectSpace = new ObjectSpace();
		objectSpace.register(localClientId, resultListener);
		objectSpace.addConnection(client);

		SlaveActionListener actionListener = ObjectSpace.getRemoteObject(client,
				254, SlaveActionListener.class);

		if (actionListener == null)
			Log.error("Client", "Der actionListener des Spielers %d ist null",
					localClientId);

		this.actionHandler = new ClientsideActionHandler(localClientId,
				actionListener);

		actionHandler.requestGameData();

		Log.info("Client", "RMI-Netzwerkverbindung zum Server eingerichtet");
	}

	@Override
	public void disconnect() {
		eventBus.unregister(this);
		objectSpace.close();
		super.disconnect();
	}

	/**
	 * Initializes the session. The {@linkplain de.gg.game.types game assets}
	 * have to get loaded first.
	 *
	 * @param sessionSetup
	 * @param players
	 * @param savedGame
	 */
	public void initGameSession(GameSessionSetup sessionSetup,
			HashMap<Short, LobbyPlayer> players,
			@Nullable SavedGame savedGame) {
		session = new SlaveSession(eventBus, sessionSetup, localClientId);
		session.init(players, savedGame);

		resultListener.setSession(session);

		Log.info("Client", "Match initialisiert");
	}

	/**
	 * Updates the client and its session.
	 */
	public void update() {
		if (session.update())
			eventBus.post(new RoundEndEvent());
	}

	/**
	 * @return the action handler used to relay the client's action to the
	 *         server.
	 */
	public ClientsideActionHandler getActionHandler() {
		return actionHandler;
	}

	@Subscribe
	public void onNotificationCreation(NotificationCreationEvent ev) {
		notifications.add(ev.getData());
		eventBus.post(new NewNotificationEvent(ev.getData()));
	}

	/**
	 * @return A list of all notifications for this client.
	 * @see NotificationCreationEvent The event to post to create a new
	 *      notification.
	 */
	public List<NotificationData> getNotifications() {
		return notifications;
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

	/**
	 * @return the local player.
	 */
	public Player getLocalPlayer() {
		return session.getWorld().getPlayers().get(localClientId);
	}

	/**
	 * @return the current game session.
	 */
	public GameSession getSession() {
		return session;
	}

}
