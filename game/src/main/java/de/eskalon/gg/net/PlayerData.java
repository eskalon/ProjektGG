package de.eskalon.gg.net;

import de.eskalon.commons.lang.ILocalized;
import de.eskalon.commons.net.packets.data.IReadyable;
import de.eskalon.gg.simulation.model.types.PlayerIcon;
import de.eskalon.gg.simulation.model.types.Religion;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class describes a client in the lobby. It is also used by the server to
 * save the ready state of its clients.
 */
@NoArgsConstructor
@EqualsAndHashCode
public class PlayerData implements ILocalized, IReadyable {

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
	private @Getter @Setter String name, surname;
	private @Getter @Setter PlayerIcon icon;
	private @Getter @Setter boolean male;
	private @Getter @Setter boolean ready;
	private @Getter @Setter Religion religion = Religion.values()[0];
	private @Getter @Setter int professionTypeIndex;
	private @Getter @Setter String hostname; // only used on the server side

	/**
	 * Toggles the ready status. Useful in the lobby.
	 */
	public void toggleReady() {
		setReady(!isReady());
	}

	@Override
	public String getLocalizedName() {
		return name + " " + surname;
	}

}
