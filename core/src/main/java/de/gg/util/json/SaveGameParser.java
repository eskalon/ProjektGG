package de.gg.util.json;

import com.google.gson.GsonBuilder;

import de.gg.game.type.BuildingTypes;
import de.gg.game.type.BuildingTypes.BuildingType;
import de.gg.game.type.CartTypes;
import de.gg.game.type.CartTypes.CartType;
import de.gg.game.type.CrimeTypes;
import de.gg.game.type.CrimeTypes.CrimeType;
import de.gg.game.type.ItemTypes;
import de.gg.game.type.ItemTypes.ItemType;
import de.gg.game.type.LawTypes;
import de.gg.game.type.LawTypes.LawType;
import de.gg.game.type.NPCCharacterTraits;
import de.gg.game.type.NPCCharacterTraits.CharacterTrait;
import de.gg.game.type.PlayerTasks;
import de.gg.game.type.PlayerTasks.PlayerTask;
import de.gg.game.type.PositionTypes;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.game.type.ProfessionTypes;
import de.gg.game.type.ProfessionTypes.ProfessionType;
import de.gg.game.type.SocialStatusS;
import de.gg.game.type.SocialStatusS.SocialStatus;

public class SaveGameParser extends SimpleJSONParser {

	/**
	 * Creates a parser for save games.
	 * <p>
	 * Has to get called after the game's {@linkplain de.gg.game.type types}
	 * have been loaded.
	 */
	public SaveGameParser() {
		super(new GsonBuilder()
				// Building Types
				.registerTypeAdapter(BuildingType.class,
						new JSONIndexSerializer<>(BuildingTypes.getValues()))
				.registerTypeAdapter(BuildingType.class,
						new JSONIndexDeserializer<>(BuildingTypes.getValues()))
				// Law Types
				.registerTypeAdapter(LawType.class,
						new JSONIndexSerializer<>(LawTypes.getValues()))
				.registerTypeAdapter(LawType.class,
						new JSONIndexDeserializer<>(LawTypes.getValues()))
				// NPC Character Trait
				.registerTypeAdapter(CharacterTrait.class,
						new JSONIndexSerializer<>(
								NPCCharacterTraits.getValues()))
				.registerTypeAdapter(CharacterTrait.class,
						new JSONIndexDeserializer<>(
								NPCCharacterTraits.getValues()))
				// Player Tasks
				.registerTypeAdapter(PlayerTask.class,
						new JSONIndexSerializer<>(PlayerTasks.getValues()))
				.registerTypeAdapter(PlayerTask.class,
						new JSONIndexDeserializer<>(PlayerTasks.getValues()))
				// Position Type
				.registerTypeAdapter(PositionType.class,
						new JSONIndexSerializer<>(PositionTypes.getValues()))
				.registerTypeAdapter(PositionType.class,
						new JSONIndexDeserializer<>(PositionTypes.getValues()))
				// Item Type
				.registerTypeAdapter(ItemType.class,
						new JSONIndexSerializer<>(ItemTypes.getValues()))
				.registerTypeAdapter(ItemType.class,
						new JSONIndexDeserializer<>(ItemTypes.getValues()))
				// Crime Type
				.registerTypeAdapter(CrimeType.class,
						new JSONIndexSerializer<>(CrimeTypes.getValues()))
				.registerTypeAdapter(CrimeType.class,
						new JSONIndexDeserializer<>(CrimeTypes.getValues()))
				// Cart Type
				.registerTypeAdapter(CartType.class,
						new JSONIndexSerializer<>(CartTypes.getValues()))
				.registerTypeAdapter(CartType.class,
						new JSONIndexDeserializer<>(CartTypes.getValues()))
				// Profession Type
				.registerTypeAdapter(ProfessionType.class,
						new JSONIndexSerializer<>(ProfessionTypes.getValues()))
				.registerTypeAdapter(ProfessionType.class,
						new JSONIndexDeserializer<>(
								ProfessionTypes.getValues()))
				// Social Status
				.registerTypeAdapter(SocialStatus.class,
						new JSONIndexSerializer<>(SocialStatusS.getValues()))
				.registerTypeAdapter(SocialStatus.class,
						new JSONIndexDeserializer<>(
								SocialStatusS.getValues())));
	}

}
