package de.gg.game;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.gg.data.RoundEndData;

/**
 * A result listener stub for the server to easily distribute an event to all
 * clients.
 * <p>
 * Takes care of calling the appropriate listener methods of every
 * {@linkplain AuthoritativeSession#getResultListeners() registered result
 * listener}.
 */
public class ServerAuthoritativResultListenerStub
		implements AuthoritativeResultListener {

	private AuthoritativeSession serverSession;
	private ExecutorService executor;

	public ServerAuthoritativResultListenerStub(
			AuthoritativeSession serverSession) {
		this.serverSession = serverSession;

		// damit alle bearbeiteten Anfragen nacheinander an die Clienten verteil
		// werden
		this.executor = Executors.newSingleThreadExecutor();
		// this.executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
	}

	@Override
	public void onAllPlayersReadied() {
		executor.submit(new AuthoritativeResultListenerRunnable() {
			@Override
			protected void informListener(
					AuthoritativeResultListener resultListener) {
				resultListener.onAllPlayersReadied();
			}
		});
	}

	@Override
	public void onRoundEnd(RoundEndData data) {
		executor.submit(new AuthoritativeResultListenerRunnable() {
			@Override
			protected void informListener(
					AuthoritativeResultListener resultListener) {
				resultListener.onRoundEnd(data);
			}
		});
	}

	@Override
	public void onCharacterDeath(short characterId) {
		executor.submit(new AuthoritativeResultListenerRunnable() {
			@Override
			protected void informListener(
					AuthoritativeResultListener resultListener) {
				resultListener.onCharacterDeath(characterId);
			}
		});
	}

	@Override
	public void onCharacterDamage(short characterId, short damage) {
		executor.submit(new AuthoritativeResultListenerRunnable() {
			@Override
			protected void informListener(
					AuthoritativeResultListener resultListener) {
				resultListener.onCharacterDamage(characterId, damage);
			}
		});
	}

	@Override
	public void onPlayerIllnessChange(short playerId, boolean isIll) {
		executor.submit(new AuthoritativeResultListenerRunnable() {
			@Override
			protected void informListener(
					AuthoritativeResultListener resultListener) {
				resultListener.onPlayerIllnessChange(playerId, isIll);
			}
		});
	}

	/**
	 * This runnable is used to easily inform every
	 * {@linkplain AuthoritativeSession#resultListeners result listener} on a
	 * separate thread.
	 */
	public abstract class AuthoritativeResultListenerRunnable
			implements Runnable {

		@Override
		public void run() {
			for (AuthoritativeResultListener resultListener : serverSession
					.getResultListeners().values()) {
				informListener(resultListener);
			}
		}

		protected abstract void informListener(
				AuthoritativeResultListener resultListener);
	}

}
