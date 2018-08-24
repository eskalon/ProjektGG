package de.gg.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.gg.game.type.PlayerIcon;
import de.gg.game.type.PlayerStubs;
import de.gg.game.type.PlayerStubs.PlayerStub;
import de.gg.game.type.ProfessionTypes;
import de.gg.network.LobbyPlayer;

/**
 * This class contains utility methods for working with players.
 */
public class PlayerUtils {

	private PlayerUtils() {
		// not used
	}

	/**
	 * @return A list of all unused icons.
	 */
	public static List<PlayerIcon> getAvailableIcons(
			Collection<LobbyPlayer> players) {
		List<PlayerIcon> tmp = new ArrayList<>();

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
	 * @return A list of all unused professions indices.
	 */
	public static List<Integer> getAvailableProfessionIndices(
			Collection<LobbyPlayer> players) {
		List<Integer> tmp = new ArrayList<>();

		for (int i = 0; i < ProfessionTypes.getValues().size(); i++) {
			boolean taken = false;
			for (LobbyPlayer p : players) {
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
	 * @return A random index for the player list.
	 * @see #VALUES
	 */
	private static int getRandomIndex() {
		return RandomUtils.getRandomNumber(0,
				PlayerStubs.getValues().size() - 1);
	}

	/**
	 * Returns a random player with an unused icon and profession.
	 *
	 * @param collection
	 *            A collection of all of the already used players to specify the
	 *            unused player icons.
	 * @return The random player.
	 */
	public static LobbyPlayer getRandomPlayerWithUnusedProperties(
			Collection<LobbyPlayer> players) {
		PlayerStub stub = PlayerStubs.getValues().get(getRandomIndex());

		return new LobbyPlayer(stub.name, stub.surname,
				getAvailableIcons(players).get(0),
				getAvailableProfessionIndices(players).get(0), stub.isMale);
	}

}
