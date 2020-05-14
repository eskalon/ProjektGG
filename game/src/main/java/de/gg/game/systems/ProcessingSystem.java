package de.gg.game.systems;

import de.gg.game.model.World;

/**
 * The child classes of this class are used to process the game.
 *
 * @param <E>
 *            The type of entity this system processes.
 */
public abstract class ProcessingSystem<E> {

	private boolean wasProcessed = false;
	private int tickRate;
	private boolean isProcessedContinuously;

	public ProcessingSystem(int tickRate, boolean isProcessedContinuously) {
		this.tickRate = tickRate;
		this.isProcessedContinuously = isProcessedContinuously;
	}

	public ProcessingSystem() {
		this(150, true);
	}

	/**
	 * Is called to initialize the system.
	 *
	 * @param world
	 * @param seed
	 */
	public void init(World world, long seed) {
	}

	public boolean wasProcessed() {
		return wasProcessed;
	}

	public void setAsProcessed(boolean wasProcessed) {
		this.wasProcessed = wasProcessed;
	}

	/**
	 * I responsible for processing one entity. Is called either once per round
	 * or at a steady tick rate depending on {@link #isProcessedContinuously()}.
	 *
	 * @param id
	 *            The id of the entity in question.
	 * @param e
	 *            The entity itself.
	 */
	public abstract void process(short id, E e);

	/**
	 * @return whether this system is processed continuously in the given
	 *         {@linkplain #getTickRate() tick rate} or once after the tick rate
	 *         passed.
	 */
	public boolean isProcessedContinuously() {
		return isProcessedContinuously;
	}

	/**
	 * @return the tick rate at which this systems
	 *         {@link #process(Object)}-method is called. If this system is not
	 *         {@linkplain #isProcessedContinuously() processed continuously} it
	 *         is processed once after this tick rate passed. Only multiples of
	 *         five are applied correctly.
	 */
	public int getTickRate() {
		return tickRate;
	}

}
