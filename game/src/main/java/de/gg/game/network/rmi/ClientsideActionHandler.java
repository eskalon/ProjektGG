package de.gg.game.network.rmi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.damios.guacamole.concurrent.DaemonThreadFactory;
import de.damios.guacamole.func.BooleanConsumer;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.gg.game.model.types.PositionType;
import de.gg.game.network.PlayerData;

/**
 * This class is a convenience wrapper for {@link SlaveActionListener}. It is
 * used to relay the player actions to the server.
 */
public class ClientsideActionHandler {

	private static final Logger LOG = LoggerService
			.getLogger(ClientsideActionHandler.class);

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

	public void changeLocalPlayer(PlayerData player) {
		executor.submit(
				() -> actionListener.onPlayerChanged(networkId, player));
	}

	public void readyUp() {
		executor.submit(() -> {
			if (!actionListener.readyUp(networkId))
				LOG.error("[CLIENT] Fehler beim \"auf Bereit stellen\"");
			else
				LOG.info("[CLIENT] Client ist bereit");
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

	public void applyForPosition(PositionType t, BooleanConsumer callback) {
		executor.submit(() -> callback
				.accept(actionListener.onAppliedForPosition(t, networkId)));
	}

	public void arrangeImpeachmentVote(short targetCharacterId,
			BooleanConsumer callback) {
		executor.submit(() -> callback.accept(actionListener
				.onImpeachmentVoteArranged(targetCharacterId, networkId)));
	}

}
