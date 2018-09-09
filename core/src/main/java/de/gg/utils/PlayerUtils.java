package de.gg.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.google.common.reflect.TypeToken;

import de.gg.game.types.PlayerIcon;
import de.gg.game.types.ProfessionType;
import de.gg.network.LobbyPlayer;
import de.gg.utils.asset.JSON;
import de.gg.utils.asset.JSONLoader.JSONLoaderParameter;
import de.gg.utils.json.SimpleJSONParser;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This class contains utility methods for working with players.
 */
public class PlayerUtils {

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> PLAYER_PRESETS_JSON_PATH() {
		return new AssetDescriptor<>("data/misc/player_presets.json",
				JSON.class,
				new JSONLoaderParameter(new TypeToken<ArrayList<PlayerStub>>() {
				}.getType()));
	}

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

		for (int i = 0; i < ProfessionType.values().length; i++) {
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
	 * Returns a random player with an unused icon and profession.
	 *
	 * @param A
	 *            list of all available name-gender-pairs for players.
	 * @param collection
	 *            A collection of all of the already used players to specify the
	 *            unused player icons.
	 * @return The random player.
	 */
	public static LobbyPlayer getRandomPlayerWithUnusedProperties(
			List<PlayerStub> playerStubs, Collection<LobbyPlayer> players) {
		PlayerStub stub = CollectionUtils.getRandomElementInList(playerStubs,
				RandomUtils.RANDOM);

		return new LobbyPlayer(stub.name, stub.surname,
				getAvailableIcons(players).get(0),
				getAvailableProfessionIndices(players).get(0), stub.isMale);
	}

	/**
	 * This class represents the player data read via
	 * {@linkplain SimpleJSONParser json} and holds a name as well as a surname.
	 */
	public class PlayerStub {
		public String name, surname;
		public boolean isMale;
	}

}
