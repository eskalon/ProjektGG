package de.gg.game.systems.server;

import java.util.HashMap;

import javax.annotation.Nullable;

import de.gg.game.systems.ProcessingSystem;
import de.gg.network.rmi.AuthoritativeResultListener;

/**
 * The child classes of this class are used to process the game on the
 * server-side. The system's {@linkplain #getSaveState() state} is persisted in
 * the save game.
 *
 * @param <E>
 *            The type of entity this system processes.
 */
public abstract class ServerProcessingSystem<E> extends ProcessingSystem<E> {

	/**
	 * This result listener is used to distribute server-side events to the
	 * client.
	 */
	protected AuthoritativeResultListener resultListener;

	public ServerProcessingSystem(AuthoritativeResultListener resultListener,
			int tickRate, boolean isProcessedContinuously) {
		super(tickRate, isProcessedContinuously);
		this.resultListener = resultListener;
	}

	public ServerProcessingSystem(AuthoritativeResultListener resultListener) {
		super();
		this.resultListener = resultListener;
	}

	/**
	 * @return A hashmap that represents the current state of the processing
	 *         system. Is persisted in the save game. <code>null</code> if
	 *         nothing has to get saved.
	 * @see #loadSavedState(HashMap)
	 */
	public @Nullable HashMap<String, Object> getSaveState() {
		return null;
	}

	/**
	 * Loads a previously saved state of this processing system.
	 *
	 * @param savedState
	 *            The saved state. <code>null</code> if no state was saved.
	 * @see #getSaveState()
	 */
	public void loadSavedState(@Nullable HashMap<String, Object> savedState) {
		// do nothing by default
	}

}
