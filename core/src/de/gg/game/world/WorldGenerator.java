package de.gg.game.world;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

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
import de.gg.game.type.GameMaps.GameMap;
import de.gg.game.type.PositionTypes;
import de.gg.game.type.SocialStatusS;
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

		generatePlayers();

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
		city.characters.put(city.characterIndex,
				CharacterFactory.createCharacterWithStatus(random,
						RandomUtils.rollTheDice(random, 2)
								? SocialStatusS.PATRICIAN
								: SocialStatusS.CAVALIER));
		city.characters.get(city.characterIndex).setHighestPositionLevel(6);
		city.characters.get(city.characterIndex)
				.setPosition(PositionTypes.MAYOR);
		city.positions.put(PositionTypes.MAYOR,
				new Position(city.characterIndex));
		city.characterIndex++;

		// Add the other characters
		for (short i = (short) (29 + players.size()); i <= 100; i++) {
			city.characters.put(city.characterIndex,
					CharacterFactory.createRandomCharacter(random));
			city.characterIndex++;
		}
	}

	private void generatePlayers() {
		for (Entry<Short, LobbyPlayer> entry : players.entrySet()) {
			LobbyPlayer lp = entry.getValue();

			Profession profession = new Profession(lp.getProfessionType());

			Character character = CharacterFactory.createPlayerCharacter(random,
					lp.getProfessionType(), setup.getDifficulty(), lp.isMale(),
					lp.getReligion(), lp.getName(), lp.getSurname());

			// TMP-Voting Test
			character.setHighestPositionLevel(4);
			character.setPosition(PositionTypes.COUNCILMAN_1);
			city.positions.put(PositionTypes.COUNCILMAN_1,
					new Position(city.characterIndex));
			// END

			city.characters.put(city.characterIndex, character);

			// TODO 1. Skill-Werte aus LobbyPlayer hinzufügen (Reihenfolge:
			// agility, bargain, combat, crafting, rhetorical, stealth)
			// TODO 2. House-IDS setzen
			Player player = PlayerFactory.createPlayerCharacter(
					city.characterIndex, lp.getIcon(), profession, (short) 0,
					(short) 0, 1, 1, 1, 1, 1, 1);

			city.players.put(entry.getKey(), player);

			city.characterIndex++;
		}
	}

}
