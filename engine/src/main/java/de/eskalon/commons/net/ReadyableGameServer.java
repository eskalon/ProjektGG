package de.eskalon.commons.net;

import com.esotericsoftware.kryonet.Connection;

import de.damios.guacamole.concurrent.ThreadHandler;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.net.data.ServerSettings;
import de.eskalon.commons.net.packets.data.IReadyable;
import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.net.packets.sync.C2SChangePlayerPacket;
import de.eskalon.commons.net.packets.sync.S2CAllPlayersReadyMessage;

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
	protected synchronized void onPlayerChange(Connection con,
			C2SChangePlayerPacket msg) {
		super.onPlayerChange(con, msg);

		for (P p : lobbyData.getPlayers().values()) {
			if (!p.isReady())
				return;
		}

		LOG.info("[SERVER] All players are ready. Initialising next round!");

		if (broadcastServer != null)
			ThreadHandler.instance().executeRunnable(() -> {
				stopBroadcastServer();
				LOG.debug("[SERVER] Broadcast server closed");
			});

		server.sendToAllTCP(new S2CAllPlayersReadyMessage());
		onAllPlayersReady();

		for (P p : lobbyData.getPlayers().values()) {
			p.setReady(false);
		}
	}

	/* --- METHODS FOR CHILD CLASSES --- */
	protected abstract void onAllPlayersReady();

}
