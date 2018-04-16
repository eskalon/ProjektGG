package de.gg.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.reflect.TypeToken;

import de.gg.entity.Player.PlayerIcon;
import de.gg.network.LobbyPlayer;
import de.gg.util.asset.Text;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This class contains utility methods for working with players.
 */
public class PlayerUtils {

	private static List<PlayerStub> VALUES;

	@Asset(Text.class)
	private static final String PLAYER_PRESETS_JSON_PATH = "data/misc/player_presets.json";

	private PlayerUtils() {
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
		return RandomUtils.getRandomNumber(0, VALUES.size() - 1);
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
		PlayerStub stub = VALUES.get(getRandomIndex());

		return new LobbyPlayer(stub.name, stub.surname,
				PlayerUtils.getAvailableIcons(collection).get(0), stub.isMale);
	}

	/**
	 * This class represents the player data read via {@linkplain JSONParser
	 * json}.
	 */
	public static class PlayerStub {

		public String name, surname;
		public boolean isMale;

	}

	public static void finishLoading(AnnotationAssetManager assetManager) {
		VALUES = JSONParser
				.parseFromJson(
						assetManager.get(PLAYER_PRESETS_JSON_PATH, Text.class)
								.getString(),
						new TypeToken<ArrayList<PlayerStub>>() {
						}.getType());
	}

}
