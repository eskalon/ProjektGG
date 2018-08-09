package de.gg.util.json;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

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

public class SaveGameParser {

	/**
	 * The used Gson-Parser. Has to get {@linkplain #initialize() initialized}.
	 */
	private static Gson gson;

	private SaveGameParser() {
	}

	/**
	 * Initializes the save game parser.
	 * <p>
	 * Has to get called after the game's types types have been loaded.
	 */
	public static void initialize() {
		gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC)
				.setExclusionStrategies(
						new ExcludeAnnotationExclusionStrategy())
				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				// Building Types
				.registerTypeAdapter(BuildingType.class,
						new JsonIndexSerializer<>(BuildingTypes.getValues()))
				.registerTypeAdapter(BuildingType.class,
						new JsonIndexDeserializer<>(BuildingTypes.getValues()))
				// Law Types
				.registerTypeAdapter(LawType.class,
						new JsonIndexSerializer<>(LawTypes.getValues()))
				.registerTypeAdapter(LawType.class,
						new JsonIndexDeserializer<>(LawTypes.getValues()))
				// NPC Character Trait
				.registerTypeAdapter(CharacterTrait.class,
						new JsonIndexSerializer<>(
								NPCCharacterTraits.getValues()))
				.registerTypeAdapter(CharacterTrait.class,
						new JsonIndexDeserializer<>(
								NPCCharacterTraits.getValues()))
				// Player Tasks
				.registerTypeAdapter(PlayerTask.class,
						new JsonIndexSerializer<>(PlayerTasks.getValues()))
				.registerTypeAdapter(PlayerTask.class,
						new JsonIndexDeserializer<>(PlayerTasks.getValues()))
				// Position Type
				.registerTypeAdapter(PositionType.class,
						new JsonIndexSerializer<>(PositionTypes.getValues()))
				.registerTypeAdapter(PositionType.class,
						new JsonIndexDeserializer<>(PositionTypes.getValues()))
				// Item Type
				.registerTypeAdapter(ItemType.class,
						new JsonIndexSerializer<>(ItemTypes.getValues()))
				.registerTypeAdapter(ItemType.class,
						new JsonIndexDeserializer<>(ItemTypes.getValues()))

				// Crime Type
				.registerTypeAdapter(CrimeType.class,
						new JsonIndexSerializer<>(CrimeTypes.getValues()))
				.registerTypeAdapter(CrimeType.class,
						new JsonIndexDeserializer<>(CrimeTypes.getValues()))
				// Cart Type
				.registerTypeAdapter(CartType.class,
						new JsonIndexSerializer<>(CartTypes.getValues()))
				.registerTypeAdapter(CartType.class,
						new JsonIndexDeserializer<>(CartTypes.getValues()))
				// Profession Type
				.registerTypeAdapter(ProfessionType.class,
						new JsonIndexSerializer<>(ProfessionTypes.getValues()))
				.registerTypeAdapter(ProfessionType.class,
						new JsonIndexDeserializer<>(
								ProfessionTypes.getValues()))
				// Social Status
				.registerTypeAdapter(SocialStatus.class,
						new JsonIndexSerializer<>(SocialStatusS.getValues()))
				.registerTypeAdapter(SocialStatus.class,
						new JsonIndexDeserializer<>(SocialStatusS.getValues()))
				.create();
	}

	/**
	 * Parses the JSON input to the given Java type.
	 * 
	 * @param jsonInput
	 *            The JSON input as string.
	 * @param clazz
	 *            The return type.
	 * @return The parsed object.
	 * @throws JsonSyntaxException
	 *             if there is a problem processing the JSON elements.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T parseFromJson(String jsonInput, Type type)
			throws JsonSyntaxException {
		return (T) gson.fromJson(jsonInput, type);
	}

	/**
	 * Parses the Java input to the a JSON string.
	 * 
	 * @param object
	 *            The java object as input.
	 * @return The parsed string.
	 * @throws JsonSyntaxException
	 *             if there is a problem processing the JSON elements.
	 */
	public static String parseToJson(Object object) throws JsonSyntaxException {
		return gson.toJson(object);
	}

}
