package de.eskalon.commons.net;

import com.esotericsoftware.kryonet.Connection;

import de.damios.guacamole.concurrent.ThreadHandler;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.net.packets.AllPlayersReadyMessage;
import de.eskalon.commons.net.packets.data.IReadyable;
import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.net.packets.sync.ChangePlayerPacket;

/**
 * This server makes sure that every client is ready before a round can begin.
 * 
 * @param <G>
 * @param <S>
 * @param <P>
 */
public abstract class ReadyableGameServer<G, S, P extends IReadyable>
		extends SimpleGameServer<G, S, P> {

	private static final Logger LOG = LoggerService
			.getLogger(ReadyableGameServer.class);

	public ReadyableGameServer(ServerSettings serverSettings,
			LobbyData lobbyData) {
		super(serverSettings, lobbyData);
	}

	@Override
	protected void onPlayerChange(Connection con, ChangePlayerPacket msg) {
		super.onPlayerChange(con, msg);

		for (P p : lobbyData.getPlayers().values()) {
			if (!p.isReady())
				return;
		}

		LOG.info("[SERVER] All players are ready. Initialising next round!");

		if (broadcastServer != null)
			ThreadHandler.instance().executeRunnable(() -> {
				stopBroadcastServer();
				LOG.info("[SERVER] Broadcast server closed");
			});

		server.sendToAllTCP(new AllPlayersReadyMessage());
		onAllPlayersReady();

		for (P p : lobbyData.getPlayers().values()) {
			p.setReady(false);
		}
	}

	/* --- METHODS FOR CHILD CLASSES --- */
	protected abstract void onAllPlayersReady();

}
