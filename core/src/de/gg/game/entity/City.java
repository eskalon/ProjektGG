package de.gg.game.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import de.gg.data.GameMaps;
import de.gg.data.GameMaps.GameMap;
import de.gg.data.GameSessionSetup;
import de.gg.game.entity.Character.Religion;
import de.gg.game.entity.ItemTypes.ItemType;
import de.gg.game.entity.LawTypes.LawType;
import de.gg.game.entity.PositionTypes.PositionType;
import de.gg.network.LobbyPlayer;
import de.gg.render.RenderData;
import de.gg.util.RandomUtils;

public class City {

	private ModelInstance skyBox;
	private List<RenderData> staticProps;
	private BuildingSlot[] buildingSlots;

	private HashMap<Short, Character> characters;
	private HashMap<Short, Player> players;
	private List<Character> prisonPopulation;

	private HashMap<PositionType, Character> positions;
	/**
	 * A hashmap with all laws. The value object is either a boolean or an
	 * integer.
	 */
	private HashMap<LawType, Object> laws;

	private HashMap<ItemType, ItemPrice> prices;
	private List<Cart> cartsOnTour;

	public City() {
	}

	public synchronized void generate(GameSessionSetup setup,
			HashMap<Short, LobbyPlayer> players) {
		// TODO mit Hilfe des sessionSetup das Spiel aufsetzen, d.h. die
		// Spielwelt sowie die Spieler in #city einrichten

		Random random = new Random(setup.getSeed());
		GameMap map = GameMaps.getByIndex(setup.getMapId());

		this.buildingSlots = map.getBuildingSlots()
				.toArray(new BuildingSlot[0]);

		// Test:
		BuildingSlot slot = buildingSlots[0];
		Building b = new Building();
		b.setType(BuildingTypes.FORGE_1);
		slot.setBuilding(b);

		this.characters = new HashMap<>();
		this.players = new HashMap<>();

		for (short i = 1; i <= 100; i++) {
			characters.put(i, generateRandomCharacter());
		}
	}

	private static Character generateRandomCharacter() {
		Character c = new Character();
		c.setAge(RandomUtils.getRandomNumber(0, 5));
		c.setGold(RandomUtils.getRandomNumber(0, 5));
		c.setHighestPositionLevel(RandomUtils.getRandomNumber(0, 5));
		c.setHp(RandomUtils.getRandomNumber(0, 5));
		c.setMale(RandomUtils.rollTheDice(1555));
		c.setMarried(RandomUtils.rollTheDice(1555));
		c.setName("" + RandomUtils.getRandomNumber(1, 100000));
		c.setNPCTrait(NPCCharacterTraits.EVEN_TEMPERED);
		c.setPosition(PositionTypes.MAYOR);
		c.setReligion(Religion.CATHOLIC);
		c.setReputationModifiers(15);
		c.setStatus(SocialStatusS.NON_CITIZEN);
		c.setSurname("" + RandomUtils.getRandomNumber(1, 100000));

		return c;
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

	public List<Character> getPrisonPopulation() {
		return prisonPopulation;
	}

	public HashMap<PositionType, Character> getPositions() {
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
