package de.gg.event;

import de.gg.game.data.NotificationData;

/**
 * Is posted when a new notification is thrown.
 */
public class NewNotificationEvent {

	private NotificationData data;

	public NewNotificationEvent(NotificationData data) {
		this.data = data;
	}

	public NotificationData getData() {
		return data;
	}

}
