package de.gg.game.world;

import java.util.HashMap;
import java.util.Random;

import de.gg.game.data.GameDifficulty;
import de.gg.game.data.GameSessionSetup;
import de.gg.game.entity.Building;
import de.gg.game.entity.BuildingSlot;
import de.gg.game.entity.Character;
import de.gg.game.entity.Player;
import de.gg.game.entity.Position;
import de.gg.game.entity.Profession;
import de.gg.game.factory.CharacterFactory;
import de.gg.game.factory.PlayerFactory;
import de.gg.game.type.BuildingTypes;
import de.gg.game.type.GameMaps;
import de.gg.game.type.PlayerIcon;
import de.gg.game.type.PositionTypes;
import de.gg.game.type.ProfessionTypes;
import de.gg.game.type.Religion;
import de.gg.game.type.SocialStatusS;
import de.gg.game.type.GameMaps.GameMap;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.network.LobbyPlayer;
import de.gg.util.RandomUtils;

public class WorldGenerator {

	protected City city;
	protected GameSessionSetup setup;
	protected HashMap<Short, LobbyPlayer> players;
	protected Random random;
	protected GameMap map;

	public WorldGenerator(City city, GameSessionSetup setup,
			HashMap<Short, LobbyPlayer> players) {
		this.city = city;
		this.setup = setup;
		this.players = players;
		this.random = new Random(setup.getSeed());
		this.map = GameMaps.getByIndex(setup.getMapId());
	}

	public void generate() {
		generateBuildings();

		generatePlayers(city.getPlayers());

		generateCharacters();
	}

	private void generateBuildings() {
		// Building-Slots
		city.buildingSlots = map.getBuildingSlots()
				.toArray(new BuildingSlot[0]);

		// [Test] Buildings
		BuildingSlot slot = city.buildingSlots[0];
		Building b = new Building();
		b.setType(BuildingTypes.FORGE_1);
		slot.setBuilding(b);

		slot = city.buildingSlots[1];
		b = new Building();
		b.setType(BuildingTypes.TOWN_HALL);
		slot.setBuilding(b);
	}

	private void generateCharacters() {
		// Mayor
		// TODO über Position Liste iterieren
		city.characters.put((short) 1,
				CharacterFactory.createCharacterWithStatus(random,
						RandomUtils.rollTheDice(random, 2)
								? SocialStatusS.PATRICIAN
								: SocialStatusS.CAVALIER));
		city.characters.get((short) 1).setHighestPositionLevel(6);
		city.characters.get((short) 1).setPosition(PositionTypes.MAYOR);
		city.positions.put(PositionTypes.MAYOR, new Position((short) 1));

		// Add the stuff for testing the voting process
		testVotingProcess(random);

		// Add the other characters
		for (short i = (short) (29 + players.size()); i <= 100; i++) {
			city.characters.put(i,
					CharacterFactory.createRandomCharacter(random));
		}
	}

	private void generatePlayers(HashMap<Short, Player> players) {

	}

	private void testVotingProcess(Random random) {
		city.characters.put((short) 2,
				CharacterFactory.createPlayerCharacter(random,
						ProfessionTypes.SMITH, GameDifficulty.EASY, true,
						Religion.ORTHODOX, "Martin", "Luther"));
		city.characters.get((short) 2).setHighestPositionLevel(4);
		city.characters.get((short) 2).setPosition(PositionTypes.COUNCILMAN_1);
		city.positions.put(PositionTypes.COUNCILMAN_1, new Position((short) 2));
		city.players.put((short) 0,
				PlayerFactory.createPlayerCharacter((short) 2,
						PlayerIcon.ICON_1, new Profession(), (short) 0,
						(short) 0, 1, 1, 1, 1, 1, 1));
	}

}
