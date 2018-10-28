package de.gg.game.world;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import de.gg.game.entities.Building;
import de.gg.game.entities.BuildingSlot;
import de.gg.game.entities.Character;
import de.gg.game.entities.Player;
import de.gg.game.entities.Position;
import de.gg.game.entities.Profession;
import de.gg.game.factories.CharacterFactory;
import de.gg.game.factories.PlayerFactory;
import de.gg.game.network.LobbyPlayer;
import de.gg.game.session.GameSessionSetup;
import de.gg.game.types.BuildingType;
import de.gg.game.types.GameMap;
import de.gg.game.types.PositionType;
import de.gg.game.types.ProfessionType;

public class WorldGenerator {

	protected World world;
	protected GameSessionSetup setup;
	protected HashMap<Short, LobbyPlayer> players;
	protected Random random;
	protected GameMap map;

	public WorldGenerator(World world, GameSessionSetup setup,
			HashMap<Short, LobbyPlayer> players) {
		this.world = world;
		this.setup = setup;
		this.players = players;
		this.map = setup.getMap();
		this.random = new Random(setup.getSeed());
	}

	public void generate() {
		generateBuildings();

		generatePlayers();

		generateCharacters();

		// Player Vote Test
		world.getCharacter((short) 1).setPosition(PositionType.COUNCILMAN_1);
		world.positions.put(PositionType.COUNCILMAN_1, new Position((short) 1));
	}

	private void generateBuildings() {
		// Building-Slots
		world.buildingSlots = map.getData().getBuildingSlots()
				.toArray(new BuildingSlot[0]);

		// [Test] Buildings
		BuildingSlot slot = world.buildingSlots[0];
		Building b = new Building();
		b.setType(BuildingType.FORGE_1);
		slot.setBuilding(b);

		slot = world.buildingSlots[1];
		b = new Building();
		b.setType(BuildingType.TOWN_HALL);
		slot.setBuilding(b);
	}

	private void generateCharacters() {
		// Add characters that have a position
		for (PositionType posType : PositionType.values()) {
			world.characters.put(world.characterIndex, CharacterFactory
					.createCharacterForPosition(random, posType));

			world.positions.put(posType, new Position(world.characterIndex));
			world.characterIndex++;
		}

		// Add the other characters
		for (short i = (short) (29 + players.size()); i <= 100; i++) {
			world.characters.put(world.characterIndex,
					CharacterFactory.createRandomCharacter(random));
			world.characterIndex++;
		}
	}

	private void generatePlayers() {
		for (Entry<Short, LobbyPlayer> entry : players.entrySet()) {
			LobbyPlayer lp = entry.getValue();

			Profession profession = new Profession(
					ProfessionType.values()[lp.getProfessionTypeIndex()]);

			Character character = CharacterFactory.createPlayerCharacter(random,
					profession.getProfession(), setup.getDifficulty(),
					lp.isMale(), lp.getReligion(), lp.getName(),
					lp.getSurname());
			world.characters.put(world.characterIndex, character);

			// TODO 1. Skill-Werte aus LobbyPlayer hinzufügen (Reihenfolge:
			// agility, bargain, combat, crafting, rhetorical, stealth)
			// TODO 2. House-IDS setzen
			Player player = PlayerFactory.createPlayerCharacter(
					world.characterIndex, lp.getIcon(), profession, (short) 0,
					(short) 0, 1, 1, 1, 1, 1, 1);

			world.players.put(entry.getKey(), player);

			world.characterIndex++;
		}
	}

}