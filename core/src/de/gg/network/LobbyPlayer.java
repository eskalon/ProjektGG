package de.gg.network;

import de.gg.game.type.PlayerIcon;
import de.gg.game.type.ProfessionTypes.ProfessionType;
import de.gg.game.type.Religion;

/**
 * This class describes a client in the lobby. It is also used by the server to
 * save the ready state of its clients.
 */
public class LobbyPlayer {

	public LobbyPlayer() {
	}

	public LobbyPlayer(String name, String surname, PlayerIcon icon,
			ProfessionType profession, boolean male) {
		this.name = name;
		this.surname = surname;
		this.male = male;
		this.icon = icon;
		this.profession = profession;
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
	private ProfessionType profession;

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

	public ProfessionType getProfessionType() {
		return profession;
	}

	public void setProfessionType(ProfessionType profession) {
		this.profession = profession;
	}

}
