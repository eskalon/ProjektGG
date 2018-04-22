package de.gg.network;

import de.gg.game.type.PlayerIcon;

/**
 * This class describes a client in the lobby. It is alos used by the server to
 * save the ready state of its clients.
 */
public class LobbyPlayer {

	public LobbyPlayer() {
	}

	public LobbyPlayer(String name, String surname, PlayerIcon icon,
			boolean male) {
		this.name = name;
		this.surname = surname;
		this.male = male;
		this.icon = icon;
		this.ready = false;
	}

	/**
	 * The players name.
	 */
	private String surname, name;
	private PlayerIcon icon;
	private boolean male;
	private boolean ready;

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayerIcon getIcon() {
		return icon;
	}

	public void setIcon(PlayerIcon icon) {
		this.icon = icon;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	/**
	 * Denotes whether the player is ready. Used in the lobby and the end round
	 * screens.
	 */
	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	/**
	 * Toggles the ready status. Useful in the lobby.
	 */
	public void toggleReady() {
		setReady(!isReady());
	}

}
