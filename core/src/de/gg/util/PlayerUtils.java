package de.gg.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import de.gg.core.LobbyPlayer;
import de.gg.core.LobbyPlayer.PlayerIcon;

/**
 * This class contains utility methods for working with players.
 */
public class PlayerUtils {

	private static Random random = new Random();
	/**
	 * All possible random names.
	 * 
	 * @see Player#getName()
	 */
	private static String[] names = new String[]{"Franz", "Heinrich", "Marthe",
			"Ferdinand", "Luise", "Oskar", "Jan", "Pierre", "Ève", "Michael",
			"Moritz", "Gregor", "Andrej Nikolajewitsch", "Peter", "Walter"};
	/**
	 * All possible random surnames.
	 * 
	 * @see Player#getSurname()
	 */
	private static String[] surnames = new String[]{"Woyzeck", "Faust",
			"Schwerdtlein", "von Walter", "Miller", "Matzerath", "Bronski",
			"Dumaine", "Charlier", "Kohlhaas", "Jäger", "Samsa", "Bolkónski",
			"Besúchow", "Faber"};
	/**
	 * The genders to all possible names. The boolean is denoting if the person
	 * is male.
	 * 
	 * @see Player#isMale()
	 */
	private static boolean[] genders = new boolean[]{true, true, false, true,
			false, true, true, true, false, true, true, true, true, true, true};

	private PlayerUtils() {
	}

	/**
	 * @return A list of all unused icons.
	 */
	public static List<PlayerIcon> getAvailableIcons(
			Collection<LobbyPlayer> players) {
		List<PlayerIcon> tmp = new ArrayList();

		for (PlayerIcon i : PlayerIcon.values()) {
			boolean taken = false;
			for (LobbyPlayer p : players) {
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
	 * Returns whether all players set their status to ready.
	 * 
	 * @param players
	 *            The players.
	 * @return The overall ready status.
	 */
	public static boolean areAllPlayersReady(Collection<LobbyPlayer> players) {
		boolean allReady = true;

		for (LobbyPlayer p : players) {
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
	public static boolean areAllPlayersReadyExcept(
			Collection<LobbyPlayer> players, LobbyPlayer player) {
		boolean allReady = true;

		for (LobbyPlayer p : players) {
			if (p != player) {
				if (!p.isReady()) {
					allReady = false;
					break;
				}
			}
		}

		return allReady;
	}

	/**
	 * @return A random index for the player lists.
	 * @see #names
	 * @see #surnames
	 * @see #genders
	 */
	private static int getRandomIndex() {
		return RandomUtils.getRandomNumber(0, names.length - 1);
	}

	/**
	 * Returns a random player.
	 * 
	 * @param collection
	 *            A collection of all of the already used players to specify the
	 *            unused player icons.
	 * @return The random player.
	 */
	public static LobbyPlayer getRandomPlayer(
			Collection<LobbyPlayer> collection) {
		int index = getRandomIndex();

		return new LobbyPlayer(names[index], surnames[index],
				PlayerUtils.getAvailableIcons(collection).get(0),
				genders[index]);
	}

}
