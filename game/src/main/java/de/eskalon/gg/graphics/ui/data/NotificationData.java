package de.eskalon.gg.graphics.ui.data;

/**
 * This class represents a notification.
 */
public class NotificationData {

	private String title, text;
	private NotificationIcon icon;

	public NotificationData(String title, String text, NotificationIcon icon) {
		this.title = title;
		this.text = text;
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public NotificationIcon getIcon() {
		return icon;
	}

	/**
	 * The icons for the notifications.
	 */
	public enum NotificationIcon {

	}

}
