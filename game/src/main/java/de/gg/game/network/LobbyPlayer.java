package de.gg.game.network;

import de.gg.engine.lang.Localized;
import de.gg.game.types.PlayerIcon;
import de.gg.game.types.Religion;

/**
 * This class describes a client in the lobby. It is also used by the server to
 * save the ready state of its clients.
 */
public class LobbyPlayer implements Localized {

	public LobbyPlayer() {
		// default public constructor
	}

	public LobbyPlayer(String name, String surname, PlayerIcon icon,
			int professionTypeIndex, boolean male) {
		this.name = name;
		this.surname = surname;
		this.male = male;
		this.icon = icon;
		this.professionTypeIndex = professionTypeIndex;
		this.ready = false;
	}

	/**
	 * The players name.
	 */
	private String surname, name;
	private PlayerIcon icon;
	private boolean male;
	private boolean ready;
	private Religion religion = Religion.values()[0];
	private int professionTypeIndex;
	private String hostname;

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

	public Religion getReligion() {
		return religion;
	}

	public void setReligion(Religion religion) {
		this.religion = religion;
	}

	public int getProfessionTypeIndex() {
		return professionTypeIndex;
	}

	public void setProfessionTypeIndex(int professionTypeIndex) {
		this.professionTypeIndex = professionTypeIndex;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getHostname() {
		return hostname;
	}

	@Override
	public String getLocalizedName() {
		return name + " " + surname;
	}

}
