package de.eskalon.gg.simulation.model;

import java.util.Random;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;

import de.eskalon.gg.net.PlayerData;
import de.eskalon.gg.simulation.GameSetup;
import de.eskalon.gg.simulation.model.entities.Building;
import de.eskalon.gg.simulation.model.entities.BuildingSlot;
import de.eskalon.gg.simulation.model.entities.Character;
import de.eskalon.gg.simulation.model.entities.Player;
import de.eskalon.gg.simulation.model.entities.Position;
import de.eskalon.gg.simulation.model.entities.Profession;
import de.eskalon.gg.simulation.model.factories.CharacterFactory;
import de.eskalon.gg.simulation.model.factories.PlayerFactory;
import de.eskalon.gg.simulation.model.types.BuildingType;
import de.eskalon.gg.simulation.model.types.GameMap;
import de.eskalon.gg.simulation.model.types.PositionType;
import de.eskalon.gg.simulation.model.types.ProfessionType;

public class WorldGenerator {

	protected World world;
	protected GameSetup setup;
	protected IntMap<PlayerData> players;
	protected Random random;
	protected GameMap map;

	public WorldGenerator(World world, GameSetup setup,
			IntMap<PlayerData> players) {
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
		world.buildingSlots = map.getBuildingSlots()
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
		for (short i = (short) (29 + players.size); i <= 100; i++) {
			world.characters.put(world.characterIndex,
					CharacterFactory.createRandomCharacter(random));
			world.characterIndex++;
		}
	}

	private void generatePlayers() {
		for (Entry<PlayerData> entry : players.entries()) {
			PlayerData lp = entry.value;

			Profession profession = new Profession(
					ProfessionType.values()[lp.getProfessionTypeIndex()]);

			Character character = CharacterFactory.createPlayerCharacter(random,
					profession.getProfession(), setup.getDifficulty(),
					lp.isMale(), lp.getReligion(), lp.getName(),
					lp.getSurname());
			world.characters.put(world.characterIndex, character);

			// TODO 1. Add skill values from the lobby data (there order is:
			// agility, bargain, combat, crafting, rhetorical, stealth)
			// TODO 2. Set house IDs
			Player player = PlayerFactory.createPlayerCharacter(
					world.characterIndex, lp.getIcon(), profession, (short) 0,
					(short) 0, 1, 1, 1, 1, 1, 1);

			world.players.put(entry.key, player);

			world.characterIndex++;
		}
	}

}
