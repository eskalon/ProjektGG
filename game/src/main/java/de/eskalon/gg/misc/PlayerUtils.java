package de.eskalon.gg.misc;

import java.util.ArrayList;
import java.util.List;

import de.eskalon.commons.utils.RandomUtils;
import de.eskalon.gg.asset.SimpleJSONParser;
import de.eskalon.gg.net.PlayerData;
import de.eskalon.gg.simulation.model.types.PlayerIcon;
import de.eskalon.gg.simulation.model.types.ProfessionType;

/**
 * This class contains utility methods for working with players.
 */
public class PlayerUtils {

	private PlayerUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return A list of all unused icons.
	 */
	public static List<PlayerIcon> getAvailableIcons(
			Iterable<PlayerData> players) {
		List<PlayerIcon> tmp = new ArrayList<>();

		for (PlayerIcon i : PlayerIcon.values()) {
			boolean taken = false;
			for (PlayerData p : players) {
				if (p.getIcon() == i) {
					taken = true;

					break;
				}
			}

			if (!taken)
				tmp.add(i);
		}

		return tmp;
	}

	/**
	 * @return A list of all unused professions indices.
	 */
	public static List<Integer> getAvailableProfessionIndices(
			Iterable<PlayerData> players) {
		List<Integer> tmp = new ArrayList<>();

		for (int i = 0; i < ProfessionType.values().length; i++) {
			boolean taken = false;
			for (PlayerData p : players) {
				if (p.getProfessionTypeIndex() == i) {
					taken = true;

					break;
				}
			}

			if (!taken)
				tmp.add(i);
		}

		return tmp;
	}

	/**
	 * Returns whether all players set their status to ready.
	 *
	 * @param players
	 *            The players.
	 * @return The overall ready status.
	 */
	public static boolean areAllPlayersReady(Iterable<PlayerData> players) {
		boolean allReady = true;

		for (PlayerData p : players) {
			if (!p.isReady()) {
				allReady = false;
				break;
			}
		}

		return allReady;
	}

	/**
	 * Returns whether all players except one (normally the host) set their
	 * status to ready.
	 *
	 * @param players
	 *            The players.
	 * @param player
	 *            The exempt player.
	 * @return The overall ready status.
	 */
	public static boolean areAllPlayersReadyExcept(Iterable<PlayerData> players,
			PlayerData player) {
		boolean allReady = true;

		for (PlayerData p : players) {
			if (!p.equals(player)) {
				if (!p.isReady()) {
					allReady = false;
					break;
				}
			}
		}

		return allReady;
	}

	/**
	 * Returns a random player with an unused icon and profession.
	 *
	 * @param A
	 *            list of all available name-gender-pairs for players.
	 * @param collection
	 *            A collection of all of the already used players to specify the
	 *            unused player icons.
	 * @return The random player.
	 */
	public static PlayerData getRandomPlayerWithUnusedProperties(
			List<PlayerTemplate> playerStubs, Iterable<PlayerData> players) {
		PlayerTemplate stub = RandomUtils.getElement(playerStubs);

		return new PlayerData(stub.name, stub.surname,
				getAvailableIcons(players).get(0),
				getAvailableProfessionIndices(players).get(0), stub.isMale);
	}

	/**
	 * This class represents the player data read via
	 * {@linkplain SimpleJSONParser json} and holds a name as well as a surname.
	 */
	public class PlayerTemplate {
		public String name, surname;
		public boolean isMale;
	}

}
