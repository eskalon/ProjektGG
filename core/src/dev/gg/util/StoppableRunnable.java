package dev.gg.util;

public abstract class StoppableRunnable implements Runnable {
	volatile boolean stop = false;

	@Override
	public void run() {
		while (!stop) {
			doStuff();
		}
	}

	protected abstract void doStuff();

	public void stop() {
		this.stop = true;
	}

}
