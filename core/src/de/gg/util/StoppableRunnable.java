package de.gg.util;

/**
 * This class represents a runnable that can easily be {@linkplain #stop
 * stopped}.
 */
public abstract class StoppableRunnable implements Runnable {
	volatile boolean stop = false;

	@Override
	public void run() {
		while (!stop) {
			doStuff();
		}
	}

	protected abstract void doStuff();

	/**
	 * Stops the execution of the runnable.
	 */
	public void stop() {
		this.stop = true;
	}

}
