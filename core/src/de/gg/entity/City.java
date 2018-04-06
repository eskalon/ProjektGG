package de.gg.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import de.gg.data.GameMaps;
import de.gg.data.GameMaps.GameMap;
import de.gg.data.GameSessionSetup;
import de.gg.entity.ItemTypes.ItemType;
import de.gg.entity.LawTypes.LawType;
import de.gg.entity.PositionTypes.PositionType;
import de.gg.network.LobbyPlayer;
import de.gg.render.RenderData;

public class City {

	private ModelInstance skyBox;
	private List<RenderData> staticProps;
	private HashMap<Short, BuildingSlot> buildingSlots;

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

	public void generate(GameSessionSetup setup,
			HashMap<Short, LobbyPlayer> players) {
		// TODO mit Hilfe des sessionSetup das Spiel aufsetzen, d.h. die
		// Spielwelt sowie die Spieler in #city einrichten

		Random r = new Random(setup.getSeed());
		GameMap map = GameMaps.getByIndex(setup.getMapId());

		// temp:
		this.buildingSlots = new HashMap<>();
	}

	public void setSkybox(ModelInstance skyBox) {
		this.skyBox = skyBox;
	}

	public ModelInstance getSkybox() {
		return skyBox;
	}

	public HashMap<Short, BuildingSlot> getBuildingSlots() {
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
