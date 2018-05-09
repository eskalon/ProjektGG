package de.gg.game.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import de.gg.game.data.GameDifficulty;
import de.gg.game.data.GameSessionSetup;
import de.gg.game.data.vote.VoteableMatter;
import de.gg.game.factory.CharacterFactory;
import de.gg.game.factory.PlayerFactory;
import de.gg.game.type.BuildingTypes;
import de.gg.game.type.GameMaps;
import de.gg.game.type.GameMaps.GameMap;
import de.gg.game.type.ItemTypes.ItemType;
import de.gg.game.type.LawTypes.LawType;
import de.gg.game.type.PlayerIcon;
import de.gg.game.type.PositionTypes;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.game.type.ProfessionTypes;
import de.gg.game.type.Religion;
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

	private LinkedList<VoteableMatter> mattersToVoteOn = new LinkedList<>();

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

		slot = buildingSlots[1];
		b = new Building();
		b.setType(BuildingTypes.TOWN_HALL);
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

		// Add the stuff for testing the voting process
		testVotingProcess(random);
	}

	private void testVotingProcess(Random random) {
		characters.put((short) 2,
				CharacterFactory.createPlayerCharacter(random,
						ProfessionTypes.SMITH, GameDifficulty.EASY, true,
						Religion.ORTHODOX, "Martin", "Luther"));
		characters.get((short) 2).setHighestPositionLevel(4);
		characters.get((short) 2).setPosition(PositionTypes.COUNCILMAN_1);
		positions.put(PositionTypes.COUNCILMAN_1, new Position((short) 2));
		players.put((short) 0,
				PlayerFactory.createPlayerCharacter((short) 2,
						PlayerIcon.ICON_1, new Profession(), (short) 0,
						(short) 0, 1, 1, 1, 1, 1, 1));

		for (short i = 3; i <= 100; i++) {
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
			if (p.getCurrentlyPlayedCharacterId() == characterId) {
				return p;
			}
		}

		return null;
	}

	public String getFullCharacterName(short id) {
		return characters.get(id).getName() + " "
				+ characters.get(id).getSurname();
	}

	/**
	 * @return matters on which a vote is held on after this round.
	 */
	public LinkedList<VoteableMatter> getMattersToHoldVoteOn() {
		return mattersToVoteOn;
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

	public Character getCharacter(short clientId) {
		return characters.get(clientId);
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

}
