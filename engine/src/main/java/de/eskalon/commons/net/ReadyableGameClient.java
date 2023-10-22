package de.eskalon.commons.net;

import com.esotericsoftware.kryonet.Listener.TypeListener;

import de.eskalon.commons.net.packets.AllPlayersReadyMessage;
import de.eskalon.commons.net.packets.data.IReadyable;

public abstract class ReadyableGameClient<G, S, P extends IReadyable>
		extends SimpleGameClient<G, S, P> {

	public ReadyableGameClient() {
		super();

		TypeListener typeListener = new TypeListener();
		typeListener.addTypeHandler(AllPlayersReadyMessage.class,
				(con, msg) -> {
					onNextRound();

					for (P p : lobbyData.getPlayers().values()) {
						p.setReady(false);
					}
				});
		client.addListener(typeListener);
	}

	/* --- METHODS FOR CHILD CLASSES --- */
	protected abstract void onNextRound();

}
