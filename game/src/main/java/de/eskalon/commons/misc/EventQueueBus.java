/*
 * Copyright 2020 eskalon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.eskalon.commons.misc;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.eventbus.EventBus;

import de.damios.guacamole.annotations.GwtIncompatible;

/**
 * This event bus queues events and only posts them to the subscribers when
 * {@link #distributeEvents()} is called. This can be useful if events have to
 * get handled in a certain thread.
 * 
 * @author damios
 */
@GwtIncompatible
public class EventQueueBus extends EventBus {

	/**
	 * Queue of posted events. Is taken care of when {@link #distributeEvents()}
	 * is called.
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
	 * <p>
	 * The events get queued until {@link #distributeEvents()} is called.
	 */
	@Override
	public void post(Object event) {
		this.eventQueue.add(event);
	}

}