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

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;

/**
 * A simple logger for events thrown by an {@link EventBus}. Has to be
 * {@linkplain EventBus#register(Object) registered} as subscriber.
 * 
 * @author damios
 */
public class EventBusLogger {

	private static final Logger LOG = LoggerService
			.getLogger(EventBusLogger.class);

	/*@Subscribe
	public void onExceptionEvent(ExceptionEvent ev) {
		LOG.error(
				"Exception thrown by subscriber method '%s(%s)' on subscriber '%s' when dispatching event '%s'",
				ev.getSubscriberMethod().getName(),
				ev.getSubscriberMethod().getParameterTypes()[0].getSimpleName(),
				ev.getSubscriber(), ev.getEvent());
	}*/

	@Subscribe
	public void onDeadEvent(DeadEvent ev) {
		LOG.debug(
				"The event '%s' was dispatched, but there were no matching subscribers registered",
				ev.getEvent());
	}

}