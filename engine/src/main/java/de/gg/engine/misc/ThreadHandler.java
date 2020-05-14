package de.gg.engine.misc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class ThreadHandler {

	private static final ThreadHandler instance = new ThreadHandler();
	private final ExecutorService cachedPool;

	private ThreadHandler() {
		this.cachedPool = Executors.newCachedThreadPool(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				// Set all used threads to be daemons
				Thread t = Executors.defaultThreadFactory().newThread(r);
				t.setDaemon(true);
				return t;
			}
		});
	}

	public static ThreadHandler getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public Future<Void> executeRunnable(Runnable r) {
		return (Future<Void>) cachedPool.submit(r);
	}

}
