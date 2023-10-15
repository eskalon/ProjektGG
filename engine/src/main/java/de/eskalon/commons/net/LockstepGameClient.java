package de.eskalon.commons.net;

import java.util.List;

import com.esotericsoftware.kryonet.Listener.TypeListener;

import de.eskalon.commons.net.packets.data.IPlayerAction;
import de.eskalon.commons.net.packets.data.IReadyable;
import de.eskalon.commons.net.packets.data.PlayerActionsWrapper;
import de.eskalon.commons.net.packets.lockstep.ActionsDistributionPacket;
import de.eskalon.commons.net.packets.lockstep.SendPlayerActionsPacket;

public abstract class LockstepGameClient<G, S, P extends IReadyable>
		extends ReadyableGameClient<G, S, P> {

	public LockstepGameClient() {
		super();

		TypeListener typeListener = new TypeListener();
		typeListener.addTypeHandler(ActionsDistributionPacket.class,
				(con, msg) -> {
					onAllActionsReceived(msg.getTurn(), msg.getActions());
				});
		client.addListener(typeListener);
	}

	public void sendActions(int turn, List<IPlayerAction> actions) {
		client.sendTCP(new SendPlayerActionsPacket(turn, actions));
	}

	/* --- METHODS FOR CHILD CLASSES --- */
	public abstract void onAllActionsReceived(int turn,
			List<PlayerActionsWrapper> list);

}
