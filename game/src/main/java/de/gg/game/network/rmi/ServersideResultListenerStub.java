package de.gg.game.network.rmi;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.eskalon.commons.misc.DaemonThreadFactory;
import de.gg.game.model.types.PositionType;
import de.gg.game.model.votes.BallotResults;
import de.gg.game.network.GameServer;
import de.gg.game.network.LobbyPlayer;
import de.gg.game.session.AuthoritativeSession;
import de.gg.game.session.GameSessionSetup;
import de.gg.game.session.SavedGame;

/**
 * A result listener stub for the server to conveniently distribute an event to
 * all connected clients.
 * <p>
 * Takes care of calling the appropriate listener methods of every
 * {@linkplain GameServer#getResultListeners() registered result listener}.
 */
public class ServersideResultListenerStub
		implements AuthoritativeResultListener {

	private GameServer server;
	private ExecutorService executor;

	public ServersideResultListenerStub(GameServer server) {
		this.server = server;

		// only a single thread is used so results are distributed one after
		// another
		this.executor = Executors.newSingleThreadExecutor(
				new DaemonThreadFactory("ServersideResultListenerStub"));
	}

	@Override
	public void onAllPlayersReadied() {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onAllPlayersReadied();
			}
		});
	}

	@Override
	public void onServerReady() {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onServerReady();
			}
		});
	}

	@Override
	public void onCharacterDeath(short characterId) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onCharacterDeath(characterId);
			}
		});
	}

	@Override
	public void onCharacterDamage(short characterId, short damage) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onCharacterDamage(characterId, damage);
			}
		});
	}

	@Override
	public void onPlayerIllnessChange(short playerId, boolean isIll) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onPlayerIllnessChange(playerId, isIll);
			}
		});
	}

	@Override
	public void setGameSpeed(int index) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.setGameSpeed(index);
			}
		});
	}

	@Override
	public void onVoteFinished(BallotResults results) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onVoteFinished(results);
			}
		});
	}

	@Override
	public void onAppliedForPosition(short playerId, PositionType type) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onAppliedForPosition(playerId, type);
			}
		});
	}

	@Override
	public void onImpeachmentVoteArranged(short targetCharacterId,
			short callerCharacterId) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onImpeachmentVoteArranged(targetCharacterId,
						callerCharacterId);
			}
		});
	}

	@Override
	public void onGameSetup(HashMap<Short, LobbyPlayer> players,
			GameSessionSetup sessionSetup, SavedGame savedGame) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onPlayerLeft(short senderId) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onPlayerLeft(senderId);
			}
		});
	}

	@Override
	public void onPlayerJoined(short senderId, LobbyPlayer lobbyPlayer) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onPlayerJoined(senderId, lobbyPlayer);
			}
		});
	}

	@Override
	public void onLobbyPlayerChanged(short senderId, LobbyPlayer lobbyPlayer) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onLobbyPlayerChanged(senderId, lobbyPlayer);
			}
		});
	}

	@Override
	public void onChatMessage(short senderId, String message) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onChatMessage(senderId, message);
			}
		});
	}

	/* ---- Actual stuff to distribute the results ---- */
	/**
	 * This method takes care of informing every
	 * {@linkplain AuthoritativeSession#resultListeners result listener} about a
	 * result that happened on the server on another thread.
	 *
	 * @param task
	 *            The task that is used to denote the result.
	 */
	private void informClients(ResultTask task) {
		executor.submit(() -> {
			for (AuthoritativeResultListener resultListener : server
					.getResultListeners().values()) {
				task.informClient(resultListener);
			}
		});
	}

	public abstract class ResultTask {
		protected abstract void informClient(
				AuthoritativeResultListener resultListener);
	}
}
