package de.gg.network;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.gg.events.ConnectionEstablishedEvent;
import de.gg.events.ConnectionFailedEvent;
import de.gg.events.PlayerConnectedEvent;
import de.gg.game.GameSessionSetup;
import de.gg.game.types.GameDifficulty;
import de.gg.game.types.GameMap;

public class GameServerConnectionTest extends GameServerBaseTest {

	private int i = 0;
	private EventBus eventBus = new EventBus();
	private short otherId = -5;

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

		client.connect(serverVersion, "localhost", port);
		clients.add(client);
		waiter.await(3000);

		// Second client
		GameClient client2 = new GameClient(eventBus);
		i = 4;
		clients.add(client2);
		client2.connect(serverVersion, "localhost", port);
		waiter.await(3100);

		// Third client (fails)
		GameClient client3 = new GameClient(eventBus);
		i = 3;

		client3.connect(serverVersion, "localhost", port);
		waiter.await(3000);
	}

	public void testServerConnectionWrongVersion() throws TimeoutException {
		GameClient client = new GameClient(eventBus);
		i = 2;

		client.connect("abc", "localhost", port);
		waiter.await(3000);
	}

	@Subscribe
	public void onConnSuccess(ConnectionEstablishedEvent event) {
		if (i == 1) {
			waiter.resume();

			waiter.assertEquals((short) 1, event.getNetworkId());
			waiter.assertEquals(1, event.getPlayers().size());
			waiter.assertEquals(sessionSetup, event.getSettings());
		}

		if (i == 2)
			waiter.fail("wrong version not detected");

		if (i == 3)
			waiter.fail("full server is not properly handled");

		if (i == 4) {
			waiter.resume();

			if (otherId == -5) {
				otherId = event.getNetworkId();
				waiter.assertTrue(event.getNetworkId() > 1);
			} else {
				waiter.assertEquals(otherId, event.getNetworkId());
			}

			waiter.assertEquals(2, event.getPlayers().size());
			waiter.assertEquals(sessionSetup, event.getSettings());
		}
	}

	@Subscribe
	public void onConnEror(ConnectionFailedEvent event) {
		if (i == 1 || i == 4)
			waiter.fail(event.getException());

		if (i == 2)
			waiter.resume();

		if (i == 3)
			waiter.resume();
	}

	@Subscribe
	public void onPlayerCon(PlayerConnectedEvent event) {
		if (i == 4) {
			if (otherId == -5) {
				otherId = event.getNetworkId();
				waiter.assertTrue(event.getNetworkId() > 1);
			} else {
				waiter.assertEquals(otherId, event.getNetworkId());
			}
		} else
			waiter.fail("too many players connected");
	}

}
