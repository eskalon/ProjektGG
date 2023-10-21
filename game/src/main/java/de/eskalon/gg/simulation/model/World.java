package de.eskalon.gg.simulation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

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

public final class World {

	private long seed;
	private GameDifficulty difficulty;
	private GameMap map;

	private GameSpeed speed;

	private ModelInstance skyBox;
	List<BaseRenderData> staticProps = new ArrayList<>();
	BuildingSlot[] buildingSlots;

	HashMap<Short, Character> characters = new HashMap<>();
	HashMap<Short, Player> players = new HashMap<>();
	List<Short> prisonPopulation = new ArrayList<>();

	/**
	 * The currently highest used character index.
	 */
	short characterIndex = 1;

	HashMap<PositionType, Position> positions = new HashMap<>();

	HashMap<LawType, Object> laws = new HashMap<>();

	HashMap<ItemType, ItemPrice> prices = new HashMap<>();
	List<Cart> cartsOnTour;

	public World() {
	}

	public synchronized void generate(GameSetup setup,
			HashMap<Short, PlayerData> players) {
		this.difficulty = setup.getDifficulty();
		this.seed = setup.getSeed();
		this.map = setup.getMap();

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

	public void setSkybox(ModelInstance skyBox) {
		this.skyBox = skyBox;
	}

	public ModelInstance getSkybox() {
		return skyBox;
	}

	public BuildingSlot[] getBuildingSlots() {
		return buildingSlots;
	}

	public List<BaseRenderData> getStaticProps() {
		return staticProps;
	}

	public HashMap<Short, Character> getCharacters() {
		return characters;
	}

	public Character getCharacter(short charId) {
		return characters.get(charId);
	}

	public HashMap<Short, Player> getPlayers() {
		return players;
	}

	public Player getPlayer(short playerId) {
		return players.get(playerId);
	}

	public List<Short> getPrisonPopulation() {
		return prisonPopulation;
	}

	public HashMap<PositionType, Position> getPositions() {
		return positions;
	}

	public Position getPosition(PositionType type) {
		return positions.get(type);
	}

	/**
	 * @return a hashmap with all laws. The value object is either a
	 *         {@link Boolean} or an {@link Integer}.
	 */
	public HashMap<LawType, Object> getLaws() {
		return laws;
	}

	public Object getLaws(LawType type) {
		return laws.get(type);
	}

	public HashMap<ItemType, ItemPrice> getPrices() {
		return prices;
	}

	public ItemPrice getPrice(ItemType type) {
		return prices.get(type);
	}

	public List<Cart> getCartsOnTour() {
		return cartsOnTour;
	}

	public long getSeed() {
		return seed;
	}

	public GameDifficulty getDifficulty() {
		return difficulty;
	}

	public GameMap getMap() {
		return map;
	}

	public GameSpeed getGameSpeed() {
		return speed;
	}

	public void setGameSpeed(GameSpeed speed) {
		this.speed = speed;
	}

}
