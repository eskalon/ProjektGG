package de.gg.game.network.rmi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.damios.guacamole.ISimpleCallback;
import de.damios.guacamole.concurrent.DaemonThreadFactory;
import de.damios.guacamole.gdx.Log;
import de.gg.game.model.types.PositionType;
import de.gg.game.network.LobbyPlayer;

/**
 * This class is a convenience wrapper for {@link SlaveActionListener}. It is
 * used to relay the player actions to the server.
 */
public class ClientsideActionHandler {

	private short networkId;
	private SlaveActionListener actionListener;
	private ExecutorService executor;

	public ClientsideActionHandler(short networkId,
			SlaveActionListener actionListener) {
		this.networkId = networkId;
		this.actionListener = actionListener;

		this.executor = Executors.newSingleThreadExecutor(
				new DaemonThreadFactory("ClientSideActionHandler"));
	}

	public void requestGameData() {
		executor.submit(() -> actionListener.requestGameData(networkId));
	}

	public void sendChatmessage(String msg) {
		executor.submit(() -> actionListener.onChatmessageSent(networkId, msg));
	}

	public void changeLocalPlayer(LobbyPlayer player) {
		executor.submit(
				() -> actionListener.onPlayerChanged(networkId, player));
	}

	public void readyUp() {
		executor.submit(() -> {
			if (!actionListener.readyUp(networkId))
				Log.error("Client", "Fehler beim \"auf Bereit stellen\"");
			else
				Log.info("Client", "Client ist bereit");
		});
	}

	public void increaseGameSpeed() {
		executor.submit(() -> actionListener.increaseGameSpeed(networkId));
	}

	public void decreaseGameSpeed() {
		executor.submit(() -> actionListener.decreaseGameSpeed(networkId));
	}

	public void castVote(int vote) {
		executor.submit(() -> actionListener.onVoteCast(vote, networkId));
	}

	public void applyForPosition(PositionType t, ISimpleCallback callback) {
		executor.submit(() -> callback
				.call(actionListener.onAppliedForPosition(t, networkId)));
	}

	public void arrangeImpeachmentVote(short targetCharacterId,
			ISimpleCallback callback) {
		executor.submit(() -> callback.call(actionListener
				.onImpeachmentVoteArranged(targetCharacterId, networkId)));
	}

}
