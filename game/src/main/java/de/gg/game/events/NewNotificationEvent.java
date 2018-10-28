package de.gg.game.events;

import de.gg.game.ui.data.NotificationData;

/**
 * Is posted after a new notification was created.
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
