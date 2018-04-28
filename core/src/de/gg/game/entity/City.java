package de.gg.game.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import de.gg.game.data.GameSessionSetup;
import de.gg.game.factory.CharacterFactory;
import de.gg.game.type.BuildingTypes;
import de.gg.game.type.GameMaps;
import de.gg.game.type.GameMaps.GameMap;
import de.gg.game.type.ItemTypes.ItemType;
import de.gg.game.type.LawTypes.LawType;
import de.gg.game.type.PositionTypes;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.game.type.SocialStatusS;
import de.gg.network.LobbyPlayer;
import de.gg.render.RenderData;
import de.gg.util.RandomUtils;

public class City {

	private ModelInstance skyBox;
	private List<RenderData> staticProps = new ArrayList<>();
	private BuildingSlot[] buildingSlots;

	private HashMap<Short, Character> characters = new HashMap<>();
	private HashMap<Short, Player> players = new HashMap<>();
	private List<Short> prisonPopulation = new ArrayList<>();

	private HashMap<PositionType, Position> positions = new HashMap<>();
	/**
	 * A hashmap with all laws. The value object is either a boolean or an
	 * integer.
	 */
	private HashMap<LawType, Object> laws = new HashMap<>();

	private HashMap<ItemType, ItemPrice> prices = new HashMap<>();
	private List<Cart> cartsOnTour;

	public City() {
	}

	public synchronized void generate(GameSessionSetup setup,
			HashMap<Short, LobbyPlayer> players) {
		// TODO mit Hilfe des sessionSetup das Spiel aufsetzen, d.h. die
		// Spielwelt sowie die Spieler in #city einrichten

		Random random = new Random(setup.getSeed());
		GameMap map = GameMaps.getByIndex(setup.getMapId());

		// Building-Slots
		this.buildingSlots = map.getBuildingSlots()
				.toArray(new BuildingSlot[0]);

		// [Test] Buildings
		BuildingSlot slot = buildingSlots[0];
		Building b = new Building();
		b.setType(BuildingTypes.FORGE_1);
		slot.setBuilding(b);

		// Mayor
		characters.put((short) 1,
				CharacterFactory.createCharacterWithStatus(random,
						RandomUtils.rollTheDice(random, 2)
								? SocialStatusS.PATRICIAN
								: SocialStatusS.CAVALIER));
		characters.get((short) 1).setHighestPositionLevel(6);
		characters.get((short) 1).setPosition(PositionTypes.MAYOR);
		positions.put(PositionTypes.MAYOR, new Position((short) 1));

		// [Test] Characters
		for (short i = 2; i <= 100; i++) {
			characters.put(i, CharacterFactory.createCharacterWithStatus(random,
					SocialStatusS.CITIZEN));
		}
	}

	/**
	 * @param characterId
	 *            The id of the character.
	 * @return the player this character is played by. <code>Null</code> if no
	 *         player plays this character.
	 */
	public Player getPlayerByCharacterId(short characterId) {
		for (Player p : players.values()) {
			if (p.getCurrentlyPlayedCharacter() == characters
					.get(characterId)) {
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

	public List<RenderData> getStaticProps() {
		return staticProps;
	}

	public HashMap<Short, Character> getCharacters() {
		return characters;
	}

	public HashMap<Short, Player> getPlayers() {
		return players;
	}

	public List<Short> getPrisonPopulation() {
		return prisonPopulation;
	}

	public HashMap<PositionType, Position> getPositions() {
		return positions;
	}

	public HashMap<LawType, Object> getLaws() {
		return laws;
	}

	public HashMap<ItemType, ItemPrice> getPrices() {
		return prices;
	}

	public List<Cart> getCartsOnTour() {
		return cartsOnTour;
	}

}
