package de.gg.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.eventbus.EventBus;

/**
 * This event bus queues events first and only posts them to the subscribers
 * when {@link #distributeEvents()} is called. This can be useful if events have
 * to get handled in the rendering thread.
 */
public class EventQueueBus extends EventBus {

	/**
	 * Queue of posted events that the current screen should handle. Is taken
	 * care of when {@link #distributeEvents()} is called.
	 */
	private Queue<Object> eventQueue = new ConcurrentLinkedQueue<>();

	/**
	 * After this method is called the {@linkplain #eventQueue queued events}
	 * get posted to their respective subscribers.
	 */
	public void distributeEvents() {
		Object event = eventQueue.poll();
		while (event != null) {
			super.post(event);
			event = eventQueue.poll();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The events get queued until {@link #distributeEvents()} is called.
	 */
	public void post(Object event) {
		this.eventQueue.add(event);
	}

}
