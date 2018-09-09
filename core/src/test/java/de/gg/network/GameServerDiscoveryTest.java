package de.gg.network;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import de.gg.ProjektGGUnitTest;
import de.gg.game.GameSessionSetup;
import de.gg.game.types.GameDifficulty;
import de.gg.game.types.GameMap;
import de.gg.network.GameServer.IHostCallback;
import de.gg.network.ServerDiscoveryHandler.HostDiscoveryListener;
import de.gg.network.messages.DiscoveryResponsePacket;
import de.gg.utils.PlayerUtils.PlayerStub;
import net.jodah.concurrentunit.Waiter;

public class GameServerDiscoveryTest extends ProjektGGUnitTest {

	protected ServerDiscoveryHandler sdh;
	protected GameServer server;
	private Waiter waiter;

	private final int port = 55554;
	private final String serverVersion = "1.2.3";
	private final String gameName = "Test Game - ABC";
	private final int maxPlayerCount = 2;
	@Mock
	private PlayerStub stub;

	@Test
	public void testServer() throws TimeoutException {
		com.esotericsoftware.minlog.Log.INFO();
		waiter = new Waiter();
		sdh = new ServerDiscoveryHandler(2500);

		assertThrows(IllegalArgumentException.class, () -> {
			sdh.discoverHosts(-563, new HostDiscoveryListener() {
				@Override
				public void onHostDiscovered(String address,
						DiscoveryResponsePacket datagramPacket) {
				}
			});
		});

		assertThrows(NullPointerException.class, () -> {
			sdh.discoverHosts(123, null);
		});

		ServerSetup serverSetup = new ServerSetup(gameName, maxPlayerCount,
				port, true, serverVersion, true);
		GameSessionSetup sessionSetup = new GameSessionSetup(
				GameDifficulty.EASY, GameMap.BAMBERG, 25);

		server = new GameServer(serverSetup, sessionSetup, null,
				Arrays.asList(stub, stub, stub));
		server.start(new IHostCallback() {
			@Override
			public void onHostStarted(Exception e) {
				if (e != null)
					waiter.fail(e);
				else {
					waiter.resume();
				}
			}
		});

		// Wait for resume() to be called
		waiter.await(8000);
		testServerDiscovery(waiter);

		server.stop();
	}

	public void testServerDiscovery(Waiter waiter) throws TimeoutException {
		sdh.discoverHosts(GameServer.UDP_DISCOVER_PORT,
				new HostDiscoveryListener() {
					@Override
					public void onHostDiscovered(String address,
							DiscoveryResponsePacket datagramPacket) {
						waiter.assertEquals(port, datagramPacket.getPort());
						waiter.assertEquals(gameName,
								datagramPacket.getGameName());
						waiter.assertEquals(maxPlayerCount,
								datagramPacket.getMaxPlayerCount());
						waiter.assertEquals(0, datagramPacket.getPlayerCount());
						waiter.resume();
					}
				});

		waiter.await(2500);
	}

}
