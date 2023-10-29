package de.eskalon.gg.network;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import de.damios.guacamole.ICallback;
import de.eskalon.commons.net.ServerDiscoveryHandler;
import de.eskalon.commons.net.SimpleGameServer;
import de.eskalon.commons.net.ServerDiscoveryHandler.HostDiscoveryListener;
import de.eskalon.commons.net.data.ServerSettings;
import de.eskalon.commons.net.packets.S2CDiscoveryResponsePacket;
import de.eskalon.gg.LibgdxUnitTest;
import de.eskalon.gg.misc.PlayerUtils.PlayerTemplate;
import de.eskalon.gg.net.GameServer;
import de.eskalon.gg.simulation.GameSetup;
import de.eskalon.gg.simulation.model.types.GameDifficulty;
import de.eskalon.gg.simulation.model.types.GameMap;
import net.jodah.concurrentunit.Waiter;

public class GameServerDiscoveryTest extends LibgdxUnitTest {

	protected ServerDiscoveryHandler<S2CDiscoveryResponsePacket> sdh;
	protected GameServer server;
	private Waiter waiter;

	private final int port = 55554;
	private final String serverVersion = "1.2.3";
	private final String gameName = "Test Game - ABC";
	private final int maxPlayerCount = 2;
	@Mock
	private PlayerTemplate stub;

	@Test
	public void testServer() throws TimeoutException, InterruptedException {
		com.esotericsoftware.minlog.Log.INFO();
		waiter = new Waiter();
		sdh = new ServerDiscoveryHandler<>(S2CDiscoveryResponsePacket.class, 2500);

		assertThrows(IllegalArgumentException.class, () -> {
			sdh.discoverHosts(-563, new HostDiscoveryListener<>() {
				@Override
				public void onHostDiscovered(String address,
						S2CDiscoveryResponsePacket datagramPacket) {
				}
			});
		});

		assertThrows(NullPointerException.class, () -> {
			sdh.discoverHosts(123, null);
		});

		ServerSettings serverSetup = new ServerSettings(gameName,
				maxPlayerCount, port, true, serverVersion, true);
		GameSetup sessionSetup = new GameSetup(
				GameDifficulty.EASY, GameMap.BAMBERG, 25);

		server = new GameServer(serverSetup, sessionSetup, null,
				Arrays.asList(stub, stub, stub));
		server.start(new ICallback() {
			@Override
			public void onSuccess(Object param) {
				waiter.resume();
			}

			@Override
			public void onFailure(Object param) {
				waiter.fail((String) param);
			}
		});

		// Wait for resume() to be called
		waiter.await(8000);
		testServerDiscovery(waiter);

		server.stop();
	}

	public void testServerDiscovery(Waiter waiter)
			throws TimeoutException, InterruptedException {
		sdh.discoverHosts(SimpleGameServer.UDP_DISCOVER_PORT,
				new HostDiscoveryListener<>() {
					@Override
					public void onHostDiscovered(String address,
							S2CDiscoveryResponsePacket datagramPacket) {
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
