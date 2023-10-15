package de.eskalon.commons.net;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import de.eskalon.commons.net.packets.data.IReadyable;
import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.net.packets.data.PlayerActionsWrapper;
import de.eskalon.commons.net.packets.lockstep.ActionsDistributionPacket;
import de.eskalon.commons.net.packets.lockstep.SendPlayerActionsPacket;

public abstract class LockstepGameServer<G, S, P extends IReadyable>
		extends ReadyableGameServer<G, S, P> {

	IntMap<List<PlayerActionsWrapper>> commandsForTurn = new IntMap<>();

	public LockstepGameServer(ServerSettings serverSettings,
			LobbyData lobbyData) {
		super(serverSettings, lobbyData);

		TypeListener typeListener = new TypeListener();
		typeListener.addTypeHandler(SendPlayerActionsPacket.class,
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

					if (list.size() == lobbyData.getPlayers().size()) {
						onAllActionsReceived(list);
						server.sendToAllTCP(new ActionsDistributionPacket(
								msg.getTurn(), list));
						commandsForTurn.remove(msg.getTurn());
					}
				});
		server.addListener(typeListener);
	}

	/* --- METHODS FOR CHILD CLASSES --- */
	public abstract void onAllActionsReceived(List<PlayerActionsWrapper> list);

}
