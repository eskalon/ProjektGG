package dev.gg.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import dev.gg.network.Player;
import dev.gg.network.Player.PlayerIcon;

/**
 * This class contains utility methods for working with players.
 */
public class PlayerUtils {

	private static Random random = new Random();
	/**
	 * All possible random names.
	 */
	private static String[] names = new String[]{"Franz", "Heinrich", "Marthe",
			"Ferdinand", "Luise"};
	/**
	 * All possible random surnames.
	 */
	private static String[] surname = new String[]{"Woyzeck", "Faust",
			"Schwerdtlein", "von Walter", "Miller"};

	private PlayerUtils() {
	}

	/**
	 * @return A list of all unused icons.
	 */
	public static List<PlayerIcon> getAvailableIcons(
			Collection<Player> players) {
		List<PlayerIcon> tmp = new ArrayList();

		for (PlayerIcon i : PlayerIcon.values()) {
			boolean taken = false;
			for (Player p : players) {
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
	public static boolean areAllPlayersReady(Collection<Player> players) {
		boolean allReady = true;

		for (Player p : players) {
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
	 * @return The ready status.
	 */
	public static boolean areAllPlayersReadyExcept(Collection<Player> players,
			Player player) {
		boolean allReady = true;

		for (Player p : players) {
			if (p != player) {
				if (!p.isReady()) {
					allReady = false;
					break;
				}
			}
		}

		return allReady;
	}

	public static Tuple<String, String> getRandomName() {
		int index = RandomUtils.getRandomNumber(0, names.length - 1);

		return new Tuple<String, String>(names[index], surname[index]);
	}

}
