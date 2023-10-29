package de.eskalon.commons.net;

import java.util.List;

import com.esotericsoftware.kryonet.Listener.TypeListener;

import de.eskalon.commons.net.packets.data.IPlayerAction;
import de.eskalon.commons.net.packets.data.IReadyable;
import de.eskalon.commons.net.packets.data.PlayerActionsWrapper;
import de.eskalon.commons.net.packets.lockstep.S2CActionsDistributionPacket;
import de.eskalon.commons.net.packets.lockstep.C2SSendPlayerActionsPacket;

public abstract class LockstepGameClient<G, S, P extends IReadyable>
		extends ReadyableGameClient<G, S, P> {

	public LockstepGameClient() {
		super();

		TypeListener typeListener = new TypeListener();
		typeListener.addTypeHandler(S2CActionsDistributionPacket.class,
				(con, msg) -> {
					onAllActionsReceived(msg.getTurn(), msg.getActions());
				});
		client.addListener(typeListener);
	}

	public void sendActions(int turn, List<IPlayerAction> actions) {
		client.sendTCP(new C2SSendPlayerActionsPacket(turn, actions));
	}

	/* --- METHODS FOR CHILD CLASSES --- */
	public abstract void onAllActionsReceived(int turn,
			List<PlayerActionsWrapper> list);

}
