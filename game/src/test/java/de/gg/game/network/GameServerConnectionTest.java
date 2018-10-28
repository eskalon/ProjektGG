package de.gg.game.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.gg.engine.network.IClientConnectCallback;
import de.gg.engine.network.ServerSetup;
import de.gg.game.events.GameDataReceivedEvent;
import de.gg.game.events.PlayerConnectedEvent;
import de.gg.game.session.GameSessionSetup;
import de.gg.game.types.GameDifficulty;
import de.gg.game.types.GameMap;

public class GameServerConnectionTest extends GameServerBaseTest
		implements IClientConnectCallback {

	private EventBus eventBus = new EventBus();

	private int i = 0;
	private short receivedId = -5;

	@Test
	public void testServerCreation() {
		ServerSetup serverSetup = new ServerSetup(gameName, maxPlayerCount,
				port, true, serverVersion, true);
		GameSessionSetup sessionSetup = new GameSessionSetup(
				GameDifficulty.EASY, GameMap.BAMBERG, 25);

		assertThrows(NullPointerException.class, () -> {
			new GameServer(null, sessionSetup, null, Arrays.asList(stub, stub));
		});
		assertThrows(NullPointerException.class, () -> {
			new GameServer(serverSetup, null, null, Arrays.asList(stub, stub));
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new GameServer(serverSetup, sessionSetup, null,
					Arrays.asList(stub));
		});

		GameServer g = new GameServer(serverSetup, sessionSetup, null,
				Arrays.asList(stub, stub));
		assertEquals(serverSetup, g.getServerSetup());
	}

	@Test
	public void testServerConnection() throws TimeoutException {
		eventBus.register(this);
		testServerConnectionWrongVersion();
		testClientLimit();
		eventBus.unregister(this);
	}

	public void testClientLimit() throws TimeoutException {
		// First client
		GameClient client = new GameClient(eventBus);
		i = 1;

		client.connect(this, serverVersion, "localhost", port);
		clients.add(client);
		waiter.await(3000);

		waiter.assertEquals((short) 1, client.getLocalNetworkID());

		// Second client
		GameClient client2 = new GameClient(eventBus);
		i = 4;
		clients.add(client2);
		client2.connect(this, serverVersion, "localhost", port);
		waiter.await(3100);

		waiter.assertEquals(client2.getLocalNetworkID(), receivedId);

		// Third client (fails)
		GameClient client3 = new GameClient(eventBus);
		i = 3;

		client3.connect(this, serverVersion, "localhost", port);
		waiter.await(3000);
	}

	public void testServerConnectionWrongVersion() throws TimeoutException {
		GameClient client = new GameClient(eventBus);
		i = 2;

		client.connect(this, "abc", "localhost", port);
		waiter.await(3000);
	}

	@Subscribe
	public void onConnSuccess(GameDataReceivedEvent event) {
		if (i == 1) {
			waiter.resume();
			waiter.assertEquals(1, event.getPlayers().size());
			waiter.assertEquals(sessionSetup, event.getSessionSetup());
			waiter.assertNull(event.getSavedGame());
		} else if (i == 4) {
			waiter.resume();
			waiter.assertEquals(2, event.getPlayers().size());
			waiter.assertEquals(sessionSetup, event.getSessionSetup());
			waiter.assertNull(event.getSavedGame());
		} else {
			waiter.fail();
		}
	}

	@Subscribe
	public void onPlayerCon(PlayerConnectedEvent event) {
		if (i == 4) {
			receivedId = event.getNetworkId();
		} else
			waiter.fail("too many players connected");
	}

	@Override
	public void onClientConnected(String errorMessage) {
		if (i == 1) {
			waiter.assertNull(errorMessage);
		}

		if (i == 2) {
			waiter.resume();
			waiter.assertNotNull(errorMessage); // wrong version
		}

		if (i == 3) {
			waiter.resume();
			waiter.assertNotNull(errorMessage); // server full
		}

		if (i == 4) {
			waiter.assertNull(errorMessage);
		}
	}

}
