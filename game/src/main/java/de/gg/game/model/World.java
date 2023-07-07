package de.gg.game.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import de.gg.game.model.entities.BuildingSlot;
import de.gg.game.model.entities.Cart;
import de.gg.game.model.entities.Character;
import de.gg.game.model.entities.ItemPrice;
import de.gg.game.model.entities.Player;
import de.gg.game.model.entities.Position;
import de.gg.game.model.types.ItemType;
import de.gg.game.model.types.LawType;
import de.gg.game.model.types.PositionType;
import de.gg.game.model.votes.Ballot;
import de.gg.game.network.PlayerData;
import de.gg.game.session.GameSessionSetup;
import de.gg.game.ui.rendering.BaseRenderData;

public final class World {

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

	LinkedList<Ballot> mattersToVoteOn = new LinkedList<>();

	public World() {
	}

	public synchronized void generate(GameSessionSetup setup,
			HashMap<Short, PlayerData> players) {
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

	/**
	 * @return matters on which a vote is held on after this round.
	 */
	public LinkedList<Ballot> getMattersToHoldVoteOn() {
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

}
