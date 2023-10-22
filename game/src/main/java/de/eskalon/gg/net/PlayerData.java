package de.eskalon.gg.net;

import de.eskalon.commons.lang.ILocalized;
import de.eskalon.commons.net.packets.data.IReadyable;
import de.eskalon.gg.simulation.model.types.PlayerIcon;
import de.eskalon.gg.simulation.model.types.Religion;

/**
 * This class describes a client in the lobby. It is also used by the server to
 * save the ready state of its clients.
 */
public class PlayerData implements ILocalized, IReadyable {

	public PlayerData() {
		// default public constructor
	}

	public PlayerData(String name, String surname, PlayerIcon icon,
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
	// only used on the server side
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

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((icon == null) ? 0 : icon.hashCode());
		result = prime * result + (male ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + professionTypeIndex;
		result = prime * result + (ready ? 1231 : 1237);
		result = prime * result
				+ ((religion == null) ? 0 : religion.hashCode());
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerData other = (PlayerData) obj;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (icon != other.icon)
			return false;
		if (male != other.male)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (professionTypeIndex != other.professionTypeIndex)
			return false;
		if (ready != other.ready)
			return false;
		if (religion != other.religion)
			return false;
		if (surname == null) {
			if (other.surname != null)
				return false;
		} else if (!surname.equals(other.surname))
			return false;
		return true;
	}

}
