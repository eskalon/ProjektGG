package de.gg.game.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import de.gg.game.data.GameSessionSetup;
import de.gg.game.data.vote.VoteableMatter;
import de.gg.game.entity.BuildingSlot;
import de.gg.game.entity.Cart;
import de.gg.game.entity.Character;
import de.gg.game.entity.ItemPrice;
import de.gg.game.entity.Player;
import de.gg.game.entity.Position;
import de.gg.game.type.ItemTypes.ItemType;
import de.gg.game.type.LawTypes.LawType;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.network.LobbyPlayer;
import de.gg.render.RenderData;

public class City {

	private ModelInstance skyBox;
	List<RenderData> staticProps = new ArrayList<>();
	BuildingSlot[] buildingSlots;

	HashMap<Short, Character> characters = new HashMap<>();
	HashMap<Short, Player> players = new HashMap<>();
	List<Short> prisonPopulation = new ArrayList<>();

	HashMap<PositionType, Position> positions = new HashMap<>();
	/**
	 * A hashmap with all laws. The value object is either a boolean or an
	 * integer.
	 */
	HashMap<LawType, Object> laws = new HashMap<>();

	HashMap<ItemType, ItemPrice> prices = new HashMap<>();
	List<Cart> cartsOnTour;

	LinkedList<VoteableMatter> mattersToVoteOn = new LinkedList<>();

	public City() {
	}

	public synchronized void generate(GameSessionSetup setup,
			HashMap<Short, LobbyPlayer> players) {
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
