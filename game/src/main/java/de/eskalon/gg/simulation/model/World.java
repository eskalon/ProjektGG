package de.eskalon.gg.simulation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.IntMap;

import de.eskalon.gg.graphics.rendering.BaseRenderData;
import de.eskalon.gg.net.PlayerData;
import de.eskalon.gg.simulation.GameSetup;
import de.eskalon.gg.simulation.model.entities.BuildingSlot;
import de.eskalon.gg.simulation.model.entities.Cart;
import de.eskalon.gg.simulation.model.entities.Character;
import de.eskalon.gg.simulation.model.entities.ItemPrice;
import de.eskalon.gg.simulation.model.entities.Player;
import de.eskalon.gg.simulation.model.entities.Position;
import de.eskalon.gg.simulation.model.types.GameDifficulty;
import de.eskalon.gg.simulation.model.types.GameMap;
import de.eskalon.gg.simulation.model.types.GameSpeed;
import de.eskalon.gg.simulation.model.types.ItemType;
import de.eskalon.gg.simulation.model.types.LawType;
import de.eskalon.gg.simulation.model.types.PositionType;
import lombok.Getter;
import lombok.Setter;

public final class World {

	private @Getter long seed;
	private @Getter GameDifficulty difficulty;
	private @Getter GameMap map;

	private @Getter @Setter GameSpeed gameSpeed;

	private @Getter @Setter ModelInstance skybox;
	private @Getter List<BaseRenderData> staticProps = new ArrayList<>();
	@Getter
	BuildingSlot[] buildingSlots;

	@Getter
	IntMap<Character> characters = new IntMap<>();
	@Getter
	IntMap<Player> players = new IntMap<>();
	@Getter
	List<Short> prisonPopulation = new ArrayList<>();

	/**
	 * The currently highest used character index.
	 */
	@Getter
	short characterIndex = 1;

	@Getter
	HashMap<PositionType, Position> positions = new HashMap<>();

	/**
	 * A hashmap with all laws. The value object is either a {@link Boolean} or
	 * an {@link Integer}.
	 */
	@Getter
	HashMap<LawType, Object> laws = new HashMap<>();

	@Getter
	HashMap<ItemType, ItemPrice> prices = new HashMap<>();
	@Getter
	List<Cart> cartsOnTour;

	public World() {
	}

	public synchronized void generate(GameSetup setup,
			IntMap<PlayerData> players) {
		this.difficulty = setup.getDifficulty();
		this.seed = setup.getSeed();
		this.map = setup.getMap();
		this.gameSpeed = GameSpeed.NORMAL;

		WorldGenerator gen = new WorldGenerator(this, setup, players);
		gen.generate();
	}

	/**
	 * @param characterId
	 *            The id of the character.
	 * @return the player this character is played by. <code>Null</code> if no
	 *         player plays this character.
	 */
	public Player getPlayerByCharacterId(short characterId) {
		for (Player p : players.values()) {
			if (p.getCurrentlyPlayedCharacterId() == characterId) {
				return p;
			}
		}

		return null;
	}

	public Character getCharacter(short charId) {
		return characters.get(charId);
	}

	public Player getPlayer(short playerId) {
		return players.get(playerId);
	}

	public Position getPosition(PositionType type) {
		return positions.get(type);
	}

	public Object getLaw(LawType type) {
		return laws.get(type);
	}

	public ItemPrice getPrice(ItemType type) {
		return prices.get(type);
	}

}
