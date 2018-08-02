package de.gg.network.rmi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.gg.game.type.PositionTypes.PositionType;
import de.gg.util.Log;
import de.gg.util.SimpleCallback;

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
		executor.submit(new Runnable() {
			@Override
			public void run() {
				if (!actionListener.readyUp(networkId))
					Log.error("Client", "Fehler beim \"auf Bereit stellen\"");
				else
					Log.info("Client", "Client ist bereit");
			}
		});
	}

	public void increaseGameSpeed() {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				actionListener.increaseGameSpeed(networkId);
			}
		});
	}

	public void decreaseGameSpeed() {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				actionListener.decreaseGameSpeed(networkId);
			}
		});
	}

	public void castVote(int vote) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				actionListener.onVoteCast(vote, networkId);
			}
		});
	}

	public void applyForPosition(PositionType t, SimpleCallback callback) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				callback.call(
						actionListener.onAppliedForPosition(t, networkId));
			}
		});
	}

	public void arrangeImpeachmentVote(short targetCharacterId,
			SimpleCallback callback) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				callback.call(actionListener.onImpeachmentVoteArranged(
						targetCharacterId, networkId));
			}
		});
	}

}
