package de.gg.network;

import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

import de.gg.ProjektGGUnitTest;
import de.gg.game.data.GameDifficulty;
import de.gg.game.data.GameSessionSetup;
import de.gg.network.GameServer.IHostCallback;
import de.gg.network.ServerDiscoveryHandler.HostDiscoveryListener;
import de.gg.network.message.DiscoveryResponsePacket;
import net.jodah.concurrentunit.Waiter;

public class GameServerDiscoveryTest extends ProjektGGUnitTest {

	protected GameServer server;
	private int port = 55554;
	private String gameName = "Test Game";

	@Test
	public void testServer() throws TimeoutException {
		final Waiter waiter = new Waiter();

		ServerSetup serverSetup = new ServerSetup(gameName, 2, port, true,
				"1.0.0", true);
		GameSessionSetup sessionSetup = new GameSessionSetup(
				GameDifficulty.EASY, 1, 25);

		server = new GameServer(serverSetup, sessionSetup, null);
		server.start(new IHostCallback() {
			@Override
			public void onHostStarted(Exception e) {
				if (e != null)
					waiter.fail(e);
				else {
					testServerDiscovery(waiter);
				}
			}
		});

		// Wait for resume() to be called
		waiter.await(3000);

		server.stop();
	}

	public void testServerDiscovery(Waiter waiter) {
		ServerDiscoveryHandler s = new ServerDiscoveryHandler();
		s.discoverHosts(GameServer.UDP_DISCOVER_PORT,
				new HostDiscoveryListener() {
					@Override
					public void onHostDiscovered(String address,
							DiscoveryResponsePacket datagramPacket) {
						waiter.assertEquals(gameName,
								datagramPacket.getGameName());
						waiter.resume();
					}
				});
	}

}
