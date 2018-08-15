package de.gg.event;

import de.gg.game.data.NotificationData;
import de.gg.network.GameClient;

/**
 * Is posted to create a new notification.
 * 
 * @see GameClient#onNotificationCreation(NewNotificationEvent)
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
