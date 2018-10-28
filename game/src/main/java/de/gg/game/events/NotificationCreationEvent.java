package de.gg.game.events;

import de.gg.game.ui.data.NotificationData;

/**
 * Is posted to create a new notification.
 */
public class NotificationCreationEvent {

	private NotificationData data;

	public NotificationCreationEvent(NotificationData data) {
		this.data = data;
	}

	public NotificationData getData() {
		return data;
	}

}
