package de.gg.game.type;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.google.common.reflect.TypeToken;

import de.gg.util.asset.JSON;
import de.gg.util.asset.JSONLoader.JSONLoaderParameter;
import de.gg.util.json.SimpleJSONParser;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class PlayerStubs {

	private static List<PlayerStub> VALUES;

	@SuppressWarnings("serial")
	private static Type TYPE = new TypeToken<ArrayList<PlayerStub>>() {
	}.getType();

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> PLAYER_PRESETS_JSON_PATH() {
		return new AssetDescriptor<JSON>("data/misc/player_presets.json",
				JSON.class, new JSONLoaderParameter(TYPE));
	}

	private PlayerStubs() {
		// shouldn't get instantiated
	}

	/**
	 * @return a list of all available player stubs.
	 */
	public static List<PlayerStub> getValues() {
		return VALUES;
	}

	public static void initialize(AssetManager assetManager) {
		VALUES = assetManager.get(PLAYER_PRESETS_JSON_PATH()).getData(TYPE);
	}

	/**
	 * This class represents the player data read via
	 * {@linkplain SimpleJSONParser json} and holds a name as well as a surname.
	 */
	public class PlayerStub {
		public String name, surname;
		public boolean isMale;
	}

}
