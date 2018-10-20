package de.gg.network.rmi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.gg.game.types.PositionType;
import de.gg.utils.SimpleCallback;
import de.gg.utils.log.Log;

/**
 * This class is an convenience wrapper for {@link SlaveActionListener}. It is
 * used to relay the player actions to the server.
 */
public class ClientActionHandler {

	private short networkId;
	private SlaveActionListener actionListener;
	private ExecutorService executor;

	public ClientActionHandler(short networkId,
			SlaveActionListener actionListener) {
		this.networkId = networkId;
		this.actionListener = actionListener;

		this.executor = Executors.newSingleThreadExecutor();
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

	public void applyForPosition(PositionType t, SimpleCallback callback) {
		executor.submit(() -> callback
				.call(actionListener.onAppliedForPosition(t, networkId)));
	}

	public void arrangeImpeachmentVote(short targetCharacterId,
			SimpleCallback callback) {
		executor.submit(() -> callback.call(actionListener
				.onImpeachmentVoteArranged(targetCharacterId, networkId)));
	}

}
