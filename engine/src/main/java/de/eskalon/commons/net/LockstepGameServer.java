package de.eskalon.commons.net;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.net.data.ServerSettings;
import de.eskalon.commons.net.packets.data.IReadyable;
import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.net.packets.data.PlayerActionsWrapper;
import de.eskalon.commons.net.packets.lockstep.C2SSendPlayerActionsPacket;
import de.eskalon.commons.net.packets.lockstep.S2CActionsDistributionPacket;

public abstract class LockstepGameServer<G, S, P extends IReadyable>
		extends ReadyableGameServer<G, S, P> {

	private static final Logger LOG = LoggerService
			.getLogger(LockstepGameServer.class);

	IntMap<List<PlayerActionsWrapper>> commandsForTurn = new IntMap<>();

	public LockstepGameServer(ServerSettings serverSettings,
			LobbyData lobbyData) {
		super(serverSettings, lobbyData);

		TypeListener typeListener = new TypeListener();
		typeListener.addTypeHandler(C2SSendPlayerActionsPacket.class,
				(con, msg) -> {
					PlayerActionsWrapper actionsWrapper = new PlayerActionsWrapper(
							(short) con.getArbitraryData(), msg.getCommands());

					List<PlayerActionsWrapper> list = commandsForTurn
							.get(msg.getTurn());
					if (list == null) {
						list = new ArrayList<>();
						commandsForTurn.put(msg.getTurn(), list);
					}

					list.add(actionsWrapper);

					if (LoggerService.isTraceEnabled()) {
						LOG.trace(
								"[SERVER] Received actions of player %d for turn %d: %s",
								actionsWrapper.getPlayerId(), msg.getTurn(),
								actionsWrapper.getActions().stream().map(
										(o) -> o.getClass().getSimpleName())
										.collect(Collectors.joining(", ")));
					}

					if (list.size() == lobbyData.getPlayers().size) {
						LOG.trace("[SERVER] All actions received for turn %d",
								msg.getTurn());

						server.sendToAllTCP(new S2CActionsDistributionPacket(
								msg.getTurn(), list));
						onAllActionsReceived(msg.getTurn(), list);
						commandsForTurn.remove(msg.getTurn());
					}
				});
		server.addListener(typeListener);
	}

	/* --- METHODS FOR CHILD CLASSES --- */
	public abstract void onAllActionsReceived(int turn,
			List<PlayerActionsWrapper> list);

}
